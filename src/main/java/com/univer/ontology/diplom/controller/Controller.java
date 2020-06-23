package com.univer.ontology.diplom.controller;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.univer.ontology.diplom.dto.Response;
import com.univer.ontology.diplom.service.DataBaseService;
import com.univer.ontology.diplom.service.FilesStorageService;
import com.univer.ontology.diplom.service.OwlProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/")
public class Controller {
    private final OwlProcessor owlProcessor;
    private final FilesStorageService filesStorageService;
    private final DataBaseService dataBaseService;


    @Autowired
    public Controller(OwlProcessor owlProcessor, FilesStorageService filesStorageService, DataBaseService dataBaseService) {
        this.owlProcessor = owlProcessor;
        this.filesStorageService = filesStorageService;
        this.dataBaseService = dataBaseService;

    }

    @GetMapping("/create-db")
    public ResponseEntity<?> createDB(@RequestParam("file") String file) {

        try {
            dataBaseService.createDB(owlProcessor.makeCreateCommands(file));
            return ResponseEntity.status(HttpStatus.OK).body(new Response(null,null,null,null,null, null,null));
        } catch (Exception e) {
            return ResponseEntity.ok("not ok");
        }
    }

    @GetMapping("/insert-individuals")
    public ResponseEntity<?> insertIndividuals(@RequestParam("file") String file) {

        try {
            dataBaseService.insertData(owlProcessor.makeInsertCommands(file));
            return ResponseEntity.status(HttpStatus.OK).body(new Response(null,null,null,null,null,null,null));
        } catch (Exception e) {
            return ResponseEntity.ok("not ok");
        }
    }

    @PostMapping("/uploadFile")
    public ResponseEntity<Response> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String filename = filesStorageService.save(file);
            return ResponseEntity.status(HttpStatus.OK).body(owlProcessor.getNodes(filename));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(null,null,null,null,null,null, e.getMessage()));
        }
    }

    @GetMapping("/execute-sparql-query")
    public ResponseEntity<Response> executeSparqlQuery(@RequestParam("file") String file, @RequestParam("query") String query) {
        try {
            String sqlQuery = owlProcessor.sparqlToSql(file, query);
            List<String> f = QueryFactory.create(query).getResultVars();

            List<Map<String,Object>> o = dataBaseService.executeQuery(sqlQuery);
            return ResponseEntity.status(HttpStatus.OK).body(new Response(null,null,null, o, f, sqlQuery, null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(null,null,null,null,null,null, e.getMessage()));
        }
    }
}