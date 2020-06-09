package com.univer.ontology.diplom.service;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface DataBaseService {
    void createDB(List<String> commands);
    void insertData(List<String> commands);
    List<Map<String,Object>> executeQuery(String commands) throws SQLException;
    File createExcel(String command);
}
