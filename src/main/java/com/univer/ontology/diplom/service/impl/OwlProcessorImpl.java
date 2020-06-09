package com.univer.ontology.diplom.service.impl;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.univer.ontology.diplom.dto.Response;
import com.univer.ontology.diplom.service.OwlProcessor;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protegex.owl.ProtegeOWL;
import edu.stanford.smi.protegex.owl.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.sql.JDBCType;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class OwlProcessorImpl implements OwlProcessor {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OwlProcessorImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Response getNodes(String filePath)throws DataAccessException, Exception{
        String uri1 = new File("src/main/resources/uploads/"+filePath).toURI().toString();

        OWLModel owlModel = ProtegeOWL.createJenaOWLModelFromURI(uri1);

        List<String> classes = (List<String>)owlModel.getUserDefinedOWLNamedClasses().stream().map(c -> ((OWLNamedClass)c).getBrowserText()).collect(Collectors.toList());
        List<String> dprops = (List<String>)owlModel.getUserDefinedOWLDatatypeProperties().stream().map(c -> ((OWLProperty)c).getBrowserText()).collect(Collectors.toList());
        List<String> oprops = (List<String>)owlModel.getUserDefinedOWLObjectProperties().stream().map(c -> ((OWLProperty)c).getBrowserText()).collect(Collectors.toList());
        return new Response(classes, oprops, dprops, null,null,null);
    }


    @Override
    public List<String> makeCreateCommands(String file) throws DataAccessException, Exception{
        HashMap<Class, JDBCType> types = new HashMap<Class, JDBCType>();
        types.put(Integer.class, JDBCType.INTEGER);
        types.put(Float.class, JDBCType.FLOAT);
        types.put(String.class, JDBCType.VARCHAR);
        types.put(Double.class, JDBCType.DOUBLE);
        types.put(Date.class, JDBCType.DATE);
        types.put(Instance.class, JDBCType.VARCHAR);

        String uri1 = new File("test.owl").toURI().toString();

        OWLModel owlModel = ProtegeOWL.createJenaOWLModelFromURI(uri1);
        List<String> result = new ArrayList<>();

        List<OWLNamedClass> classes = (List<OWLNamedClass>)owlModel.getUserDefinedOWLNamedClasses();
        List<OWLProperty> dprops = (List<OWLProperty>)owlModel.getUserDefinedOWLDatatypeProperties();
        List<OWLProperty> oprops = (List<OWLProperty>)owlModel.getUserDefinedOWLObjectProperties();
        List<OWLIndividual> inds = (List<OWLIndividual>)owlModel.getUserDefinedRDFIndividuals(true);
        System.out.println(classes);
        System.out.println(dprops.get(0).getProtegeType());
        System.out.println(Integer.class.equals(dprops.get(0).getValueType().getJavaType()));
        System.out.println(dprops.get(0).getRDFType());
        System.out.println(dprops.get(0).getDirectType());

        System.out.println(oprops);
        System.out.println(inds);

        for (Iterator it = classes.iterator(); it.hasNext();) {
            OWLNamedClass cls = (OWLNamedClass) it.next();
            Collection instances = cls.getInstances(false);
            System.out.println("Class " + cls.getBrowserText() + " (" + instances.size() + ")");

            for (Iterator jt = instances.iterator(); jt.hasNext();) {
                OWLIndividual individual = (OWLIndividual) jt.next();
                System.out.println(" - " + individual.getBrowserText());
                for (OWLProperty o: oprops)
                    System.out.println(" -- " + individual.getPropertyValue(o));
                System.out.println(" - " + individual.getReferences());

            }
        }

        System.out.println( (List<OWLIndividual>)owlModel.getUserDefinedRDFIndividuals(true));

        for (OWLNamedClass clazz : classes) {
            String createString =
                    "CREATE TABLE " + clazz.getBrowserText()
                            +
                            "(\n" +
                            "   id_name VARCHAR(50) PRIMARY KEY      NOT NULL,\n";
            for (OWLProperty p: findProps(clazz, dprops)) {
                createString += "   " + p.getBrowserText() + " " + types.get(p.getValueType().getJavaType()) + ",\n";
            }
            for (OWLProperty p: findProps(clazz, oprops)) {
                createString += "   " + p.getBrowserText() + " " + types.get(p.getValueType().getJavaType()) + ",\n";
            }
            createString = createString.substring(0, createString.length() - 2);
            createString += ");";
            System.out.println(createString);
            result.add(createString);
          //  jdbcTemplate.execute(createString);
        }

        for (OWLProperty p: oprops) {
            String alterString = "ALTER TABLE ";
            alterString += p.getDomain(false).getBrowserText() + " ADD CONSTRAINT fk_" + p.getDomain(false).getBrowserText() + p.getRange().getBrowserText()

                    + " FOREIGN KEY (" + p.getBrowserText() + ") REFERENCES " + p.getRange().getBrowserText() + "(id_name)";
            System.out.println(alterString);
            result.add(alterString);

           // jdbcTemplate.update(alterString);
        }


        return result;
    }

    @Override
    public List<String> makeInsertCommands(String file) throws DataAccessException, Exception {
        String uri1 = new File("test.owl").toURI().toString();
        OWLModel owlModel = ProtegeOWL.createJenaOWLModelFromURI(uri1);
        List<OWLIndividual> inds = (List<OWLIndividual>)owlModel.getUserDefinedRDFIndividuals(true);
        List<String> result = new ArrayList<>();
        for (OWLIndividual in : inds) {
            String insertString = "INSERT INTO " + in.getProtegeType().getBrowserText() + "(id_name,";
            for (String s : (List<String>)in.getRDFProperties().stream()
                    .map(p -> ((RDFResource)p).getBrowserText())
                    .filter(s ->!s.equals("rdf:type"))
                    .collect(Collectors.toList())) {
                insertString += s + ",";
            }
            insertString = insertString.substring(0, insertString.length() - 1);
            insertString += ") VALUES ('" + in.getBrowserText() +"',";
            for (String s : (List<String>)in.getRDFProperties().stream()
                    .filter(p -> !((RDFProperty)p).getFrameID().getName().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"))
                    .map(p -> in.getPropertyValue((RDFProperty) p) instanceof RDFObject
                            ? ((RDFObject)in.getPropertyValue((RDFProperty) p)).getBrowserText()
                            : in.getPropertyValue((RDFProperty) p))
                    .map(p -> p = "'"+p+"'")
                    .collect(Collectors.toList()) ){
                insertString += s + ",";
            }
            insertString = insertString.substring(0, insertString.length() - 1);
            insertString +=  ")";
            System.out.println(insertString);
            result.add(insertString);
            //jdbcTemplate.update(insertString);

        }
        return result;
    }

    @Override
    public String sparqlToSql(String file, String queryString) throws DataAccessException, Exception {
//        queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
//                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
//                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
//                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
//                "PREFIX my: <http://www.semanticweb.org/shest/ontologies/2020/4/untitled-ontology-4#>\n" +
//                "SELECT ?subject ?object ?dprop \n" +
//                "\tWHERE { ?subject rdf:type  my:class2_1.\n" +
//                "\t\t?subject my:ref2_1to1_1 ?object .\n" +
//                "\t\t?object my:dprop4 ?dprop \n" +
//                "\t\t}";
        Query query = QueryFactory.create(queryString);

        List<String> resultVars = query.getResultVars();
        ElementGroup queryPattern = (ElementGroup)query.getQueryPattern();
        ElementPathBlock elementPathBlock = (ElementPathBlock) queryPattern.getElements().get(0);
        List<TriplePath> triplePaths =  elementPathBlock.getPattern().getList();
        String clazz = triplePaths.stream()
                .filter(p -> "type".equals(p.getPredicate().getLocalName()))
                .findFirst().get()
                .getObject().getLocalName();

        String selectString = "select id_name as name, ";
        for (TriplePath r: triplePaths) {
            if (resultVars.contains(r.getObject().toString().substring(1))
                    && !"type".equals(r.getPredicate().getLocalName())){
                selectString += r.getPredicate().getLocalName() + ", ";
            }

        }
        selectString = selectString.substring(0, selectString.length() - 2);
        selectString += " from " + clazz + " ";

        System.out.println(selectString);
        return selectString;
    }

    private List<OWLProperty> findProps(OWLNamedClass clazz, List<OWLProperty> dprops) {
        List<OWLProperty> result = new ArrayList<>();
        dprops.stream().filter(p -> p.getDomains(false).contains(clazz)).collect(Collectors.toList());
        result.addAll(dprops.stream().filter(p -> p.getDomains(false).contains(clazz)).collect(Collectors.toList()));
        for (OWLNamedClass superClazz : (List<OWLNamedClass>)clazz.getSuperclasses(true)) {
            result.addAll(findProps(superClazz, dprops));
        }
        return result;
    }
}
