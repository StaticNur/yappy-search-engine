package com.yappy.search_engine.service.impl;

import com.yappy.search_engine.model.VideoFromExcel;
import com.yappy.search_engine.service.ImportDataService;
import com.yappy.search_engine.service.MediaContentService;
import com.yappy.search_engine.util.parser.ExcelParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class ExcelDataServiceImpl implements ImportDataService {
    public static final String PATH_FILE = "датасет-видео-тег.xlsx";//src/main/resources/датасет-видео-тег.xlsx
    private final ExcelParser excelParser;
    private final MediaContentService mediaContentService;

    @Autowired
    public ExcelDataServiceImpl(ExcelParser excelParser, MediaContentService mediaContentService) {
        this.excelParser = excelParser;
        this.mediaContentService = mediaContentService;
    }

    @Override
    public void importData() {
        List<VideoFromExcel> videoFromExcels;
        try {
            Resource resource = new ClassPathResource(PATH_FILE);
            if (resource.exists()) {
                try(InputStream inputStream = resource.getInputStream()) {
                    videoFromExcels = excelParser.parseExcelFile(inputStream);
                    mediaContentService.saveAll(videoFromExcels);
                }
            } else {
                throw new FileNotFoundException("Файл не найден: " + PATH_FILE);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
