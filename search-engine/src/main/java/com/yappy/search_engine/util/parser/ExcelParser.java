package com.yappy.search_engine.util.parser;

import com.yappy.search_engine.model.Embedding;
import com.yappy.search_engine.model.MediaContent;
import com.yappy.search_engine.model.VideoFromExcel;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class ExcelParser {

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
            if (removeBrackets) {
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

    /*public static void main(String[] args) {
        List<MediaContent> videos = new ArrayList<>();
        String jdbcUrl = "jdbc:postgresql://localhost:5433/media-content-db";
        String username = "postgres";
        String password = "postgres";
        String sql = "SELECT tags FROM video_data.videos";
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                String tags = resultSet.getString("tags");
                MediaContent mediaContent = new MediaContent();
                mediaContent.setTags(tags);
                videos.add(mediaContent);
            }
            createEmbeddingFromUserDescription(videos);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createEmbeddingFromUserDescription(List<MediaContent> videos) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");
        // Создание стиля для заголовка
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        // Создание строки заголовка
        Row headerRow = sheet.createRow(0);
        Cell cell = headerRow.createCell(0);
        cell.setCellValue("Tags");
        cell.setCellStyle(headerStyle);

        int rowIndex = 1;
        Set<String> setTag = getSetTag(videos);
        for (String tag : setTag) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(tag);
        }

        try (FileOutputStream fileOut = new FileOutputStream("tags-split.xlsx")) {
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Set<String> getSetTag(List<MediaContent> videos) {
        Set<String> tagSet = new HashSet<>();
        for (MediaContent video : videos) {
            String tagStr = video.getTags();
            if (tagStr != null && !tagStr.isEmpty()) {
                String[] tags = tagStr.split(" ");
                for (String tag : tags) {
                    tag = tag.trim().replaceAll("#", "");
                    if(!tag.matches("\\d+") && tag.length() > 1){
                        tagSet.add(tag);
                    }
                }
            }
        }
        return tagSet;
    }*/
}
