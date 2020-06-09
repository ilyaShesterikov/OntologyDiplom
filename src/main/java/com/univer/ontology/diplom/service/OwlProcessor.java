package com.univer.ontology.diplom.service;

import com.univer.ontology.diplom.dto.Response;
import org.springframework.dao.DataAccessException;

import java.util.List;

public interface OwlProcessor {
    public Response getNodes(String filePath)throws DataAccessException, Exception;
    public List<String> makeCreateCommands(String file)throws DataAccessException, Exception;
    public List<String> makeInsertCommands(String file)throws DataAccessException, Exception;
    public String sparqlToSql(String file, String queryString)throws DataAccessException, Exception;

}
