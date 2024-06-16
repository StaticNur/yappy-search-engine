package com.yappy.search_engine.util.parser;

import com.yappy.search_engine.model.MediaContent;
import com.yappy.search_engine.model.VideoFromExcel;
import com.yappy.search_engine.out.service.impl.ExternalApiClient;
import com.yappy.search_engine.service.MediaContentService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class CreateExcel {
    private final ExternalApiClient client;

    @Autowired
    public CreateExcel(ExternalApiClient client) {
        this.client = client;
    }

    /*public static void main(String[] args) {
        List<MediaContent> videos = new ArrayList<>();
        String jdbcUrl = "jdbc:postgresql://localhost:5433/media-content-db";
        String username = "postgres";
        String password = "postgres";

        String sql = """
                SELECT id, uuid, url, title, description_user, transcription_audio, language_audio,
                 description_visual, tags, created, popularity, hash, embedding_audio, embedding_visual,
                 embedding_user_description, indexing_time
                FROM video_data.videos ORDER BY id LIMIT 10000""";

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                String url = resultSet.getString("url");
                String title = resultSet.getString("title");
                String descriptionUser = resultSet.getString("description_user");
                String transcriptionAudio = resultSet.getString("transcription_audio");
                String languageAudio = resultSet.getString("language_audio");
                String descriptionVisual = resultSet.getString("description_visual");
                String tags = resultSet.getString("tags");
                LocalDateTime created = resultSet.getObject("created", LocalDateTime.class);
                int popularity = resultSet.getInt("popularity");
                String hash = resultSet.getString("popularity");
                String embeddingAudio = resultSet.getString("embedding_audio");
                String embeddingVisual = resultSet.getString("embedding_visual");
                String embeddingUserDescription = resultSet.getString("embedding_user_description");
                Long indexingTime = resultSet.getLong("indexing_time");
                videos.add(new MediaContent(id, uuid, url, title, descriptionUser, transcriptionAudio, languageAudio,
                        descriptionVisual, tags, created, popularity, hash, embeddingAudio, embeddingVisual,
                        embeddingUserDescription, indexingTime));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            Resource resource = new ClassPathResource("датасет-видео-тег.xlsx");
            if (resource.exists()) {

                createEmbeddingFromUserDescription(videos);
            } else {
                throw new FileNotFoundException("Файл не найден: ");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }*/

    public static void createEmbeddingFromUserDescription(List<VideoFromExcel> videos) {
        /*Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");
        // Создание стиля для заголовка
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        // Создание строки заголовка
        String[] headers = {"ID", "UUID", "URL", "Title", "Description User", "Transcription Audio", "Language Audio",
                "Description Visual", "Tags", "Created", "Popularity", "Embedding Audio", "Embedding Visual",
                "Embedding User Description"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        int rowIndex = 1;
        int id = 1;
        for (MediaContent mediaContent : videos) {
            Row row = sheet.createRow(rowIndex++);

            row.createCell(0).setCellValue(id++);
            row.createCell(1).setCellValue(mediaContent.getUuid().toString());
            row.createCell(2).setCellValue(mediaContent.getUrl());
            row.createCell(3).setCellValue(mediaContent.getTitle());
            row.createCell(4).setCellValue(mediaContent.getDescriptionUser());
            row.createCell(5).setCellValue(mediaContent.getTranscriptionAudio());
            row.createCell(6).setCellValue(mediaContent.getLanguageAudio());
            row.createCell(7).setCellValue(mediaContent.getDescriptionVisual());
            row.createCell(8).setCellValue(mediaContent.getTags());
            row.createCell(9).setCellValue(mediaContent.getCreated());
            row.createCell(10).setCellValue(mediaContent.getPopularity());
            row.createCell(11).setCellValue(mediaContent.getEmbeddingAudio());
            row.createCell(12).setCellValue(mediaContent.getEmbeddingVisual());
            row.createCell(13).setCellValue(mediaContent.getEmbeddingUserDescription());

            if (rowIndex % 100 == 0) {
                System.out.println("Посчитали " + rowIndex + " embedding");
            }
        }
        try (FileOutputStream fileOut = new FileOutputStream("embedding_user_description.xlsx")) {
            workbook.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
