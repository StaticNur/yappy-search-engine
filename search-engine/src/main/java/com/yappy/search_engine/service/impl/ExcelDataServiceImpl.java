package com.yappy.search_engine.service.impl;

import com.yappy.search_engine.model.VideoFromExcel;
import com.yappy.search_engine.service.ExcelDataService;
import com.yappy.search_engine.service.MediaContentService;
import com.yappy.search_engine.util.parser.ExcelParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ExcelDataServiceImpl implements ExcelDataService {
    public static final String PATH_FILE = "src/main/resources/датасет-видео-тег.xlsx";
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
            videoFromExcels = excelParser.parseExcelFile(PATH_FILE);
            mediaContentService.saveAll(videoFromExcels);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
