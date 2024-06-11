package com.yappy.search_engine.util.parser;

import com.yappy.search_engine.model.VideoFromExcel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class ExcelParser {

    public List<VideoFromExcel> parseExcelFile(InputStream inputStream) throws IOException {
        List<VideoFromExcel> videoEntries = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rows = sheet.iterator();

        if (rows.hasNext()) {
            rows.next();
            rows.next();
        }

        while (rows.hasNext()) {
            Row currentRow = rows.next();
            Cell urlCell = currentRow.getCell(0);
            Cell descriptionCell = currentRow.getCell(1);

            String url = getCellValueAsString(urlCell);
            String description = getCellValueAsString(descriptionCell);

            videoEntries.add(new VideoFromExcel(url, description));
        }

        workbook.close();
        return videoEntries;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}
