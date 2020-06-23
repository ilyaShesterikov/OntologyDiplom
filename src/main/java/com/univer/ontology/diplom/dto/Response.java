package com.univer.ontology.diplom.dto;

import java.util.List;
import java.util.Map;

public class Response {
    List<String> classes;
    List<String> oprops;
    List<String> dprops;
    List<Map<String,Object>> fields;
    List<String> col;
    String sqlQuery;
    String error;

    public Response(List<String> classes, List<String> oprops, List<String> dprops, List<Map<String, Object>> fields, List<String> col, String sqlQuery, String error) {
        this.classes = classes;
        this.oprops = oprops;
        this.dprops = dprops;
        this.fields = fields;
        this.col = col;
        this.sqlQuery = sqlQuery;
        this.error = error;
    }

    public List<String> getClasses() {
        return classes;
    }

    public void setClasses(List<String> classes) {
        this.classes = classes;
    }

    public List<String> getOprops() {
        return oprops;
    }

    public void setOprops(List<String> oprops) {
        this.oprops = oprops;
    }

    public List<String> getDprops() {
        return dprops;
    }

    public void setDprops(List<String> dprops) {
        this.dprops = dprops;
    }

    public List<Map<String, Object>> getFields() {
        return fields;
    }

    public void setFields(List<Map<String, Object>> fields) {
        this.fields = fields;
    }

    public List<String> getCol() {
        return col;
    }

    public void setCol(List<String> col) {
        this.col = col;
    }

    public String getSqlQuery() {
        return sqlQuery;
    }

    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
