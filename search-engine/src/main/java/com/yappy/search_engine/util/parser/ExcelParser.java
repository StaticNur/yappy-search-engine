package com.yappy.search_engine.util.parser;

import com.yappy.search_engine.model.Embedding;
import com.yappy.search_engine.model.VideoFromExcel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class ExcelParser {

    /*public static void main(String[] args) {
        List<Embedding> embeddingAudios;
        try {
            Resource resource = new ClassPathResource("Mclip_tags_11000.xlsx");
            if (resource.exists()) {
                try(InputStream inputStream = resource.getInputStream()) {
                    ExcelParser excelParser = new ExcelParser();
                    embeddingAudios = excelParser.parseEmbeddingExcelFile(inputStream, true);
                    for (int i=0;i<10;i++){
                        System.out.println(embeddingAudios.get(i));
                    }
                }
            } else {
                throw new FileNotFoundException("Файл не найден: ");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }*/

    public List<VideoFromExcel> parseMainExcelFile(InputStream inputStream) throws IOException {
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

    public List<Embedding> parseEmbeddingExcelFile(InputStream inputStream, boolean removeBrackets) throws IOException {
        List<Embedding> videoEntries = new ArrayList<>();

        IOUtils.setByteArrayMaxOverride(250000000);
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rows = sheet.iterator();

        if (rows.hasNext()) {
            rows.next();
        }

        while (rows.hasNext()) {
            Row currentRow = rows.next();
            Cell urlCell = currentRow.getCell(1);
            Cell transcriptionCell = currentRow.getCell(2);
            Cell embeddingCell = currentRow.getCell(3);

            String url = getCellValueAsString(urlCell);
            String transcription = getCellValueAsString(transcriptionCell);
            String embedding = getCellValueAsString(embeddingCell);
            String modifiedString = embedding;
            if(removeBrackets){
                modifiedString = embedding.trim().substring(1, embedding.length() - 1);
            }
            videoEntries.add(new Embedding(url.trim(), transcription.trim(), modifiedString));
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
