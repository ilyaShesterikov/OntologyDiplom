package com.univer.ontology.diplom.service.impl;

import com.univer.ontology.diplom.service.DataBaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class DataBaseServiceImpl implements DataBaseService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DataBaseServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void createDB(List<String> commands) {
        for (String command : commands) {
            jdbcTemplate.execute(command);
        }
    }

    @Override
    public void insertData(List<String> commands) {
        for (String command : commands) {
            jdbcTemplate.update(command);
        }
    }

    @Override
    public List<Map<String,Object>> executeQuery(String command) throws SQLException {
        return jdbcTemplate.queryForList(command);
    }

    @Override
    public File createExcel(String command) {
        return null;//jdbcTemplate.query(command, Row);
    }

//    public HSSFWorkbook dump(ResultSet resultSet) throws SQLException {
//        HSSFWorkbook workbook = new HSSFWorkbook();
//        HSSFFont boldFont = workbook.createFont();
//        HSSFSheet sheet = workbook.createSheet("sheet");
//        HSSFRow titleRow = sheet.createRow(0);
//        ResultSetMetaData metaData = resultSet.getMetaData();
//        int columnCount = metaData.getColumnCount();
//        for (int colIndex = 0; colIndex < columnCount; colIndex++) {
//            String title = metaData.getColumnLabel(colIndex + 1);
//            HSSFCell cell = HSSFCellUtil.createCell(titleRow, colIndex, title);
//            HSSFCellStyle style = workbook.createCellStyle();
//            style.setFont(boldFont);
//            cell.setCellStyle(style);
//        }
//        dumpData(resultSet,sheet,columnCount);
//        return workbook;
//    }
//
//    private void dumpData(ResultSet resultSet, HSSFSheet sheet, int columnCount) throws SQLException {
//        int currentRow = 1;
//        resultSet.beforeFirst();
//        while (resultSet.next()) {
//            HSSFRow row = sheet.createRow(currentRow++);
//            for (int colIndex = 0; colIndex < columnCount; colIndex++) {
//                Object value = resultSet.getObject(colIndex + 1);
//                final HSSFCell cell = row.createCell(colIndex);
//                if (value == null) {
//                    cell.setCellValue("");
//                } else {
//                    if (value instanceof Calendar) {
//                        cell.setCellValue((Calendar) value);
//                    } else if (value instanceof Date) {
//                        cell.setCellValue((Date) value);
//                    } else if (value instanceof String) {
//                        cell.setCellValue((String) value);
//                    } else if (value instanceof Boolean) {
//                        cell.setCellValue((Boolean) value);
//                    } else if (value instanceof Double) {
//                        cell.setCellValue((Double) value);
//                    } else if (value instanceof BigDecimal) {
//                        cell.setCellValue(((BigDecimal) value).doubleValue());
//                    }
//                }
//            }
//        }
//        for (int i = 0; i < columnCount; i++) {
//            sheet.autoSizeColumn(i);
//        }
//    }
}
