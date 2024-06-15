package com.yappy.search_engine.util.parser;

import com.yappy.search_engine.model.VideoFromExcel;
import com.yappy.search_engine.out.service.impl.ExternalApiClient;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class CreateExcel {
    private final ExternalApiClient client;

    @Autowired
    public CreateExcel(ExternalApiClient client) {
        this.client = client;
    }

    public void createEmbeddingFromUserDescription(List<VideoFromExcel> videos) {
        Resource resource = new ClassPathResource("embedding_user_description.xlsx");

        try (FileInputStream fileIn = new FileInputStream(resource.getFile());
             Workbook workbook = new XSSFWorkbook(fileIn)) {

            Sheet sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();

            for (int i = 0; i < videos.size(); i++) {
                if (videos.get(i).getDescription() != null && !videos.get(i).getDescription().trim().isBlank()) {
                    Row row = sheet.createRow(++lastRowNum);
                    Cell urlCell = row.createCell(0);
                    urlCell.setCellValue(videos.get(i).getUrl());

                    Cell dataCell = row.createCell(1);
                    double[] embedding = client.getEmbedding(videos.get(i).getDescription());
                    dataCell.setCellValue(Arrays.toString(embedding));
                    if (lastRowNum % 10000 == 0) {
                        System.out.println("Посчитали " + lastRowNum + " embedding");
                    }
                    lastRowNum++;
                }
            }
            try (FileOutputStream fileOut = new FileOutputStream(resource.getFile())) {
                workbook.write(fileOut);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
