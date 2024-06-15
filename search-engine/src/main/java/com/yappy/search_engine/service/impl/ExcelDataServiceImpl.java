package com.yappy.search_engine.service.impl;

import com.yappy.search_engine.helper.TagFrequencyCalculationService;
import com.yappy.search_engine.mapper.ExcelDataMapper;
import com.yappy.search_engine.model.EmbeddingAudio;
import com.yappy.search_engine.model.MediaContent;
import com.yappy.search_engine.model.VideoFromExcel;
import com.yappy.search_engine.service.ImportExcelService;
import com.yappy.search_engine.service.MediaContentService;
import com.yappy.search_engine.util.parser.CreateExcel;
import com.yappy.search_engine.util.parser.ExcelParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
public class ExcelDataServiceImpl implements ImportExcelService {
    public static final String PATH_FILE_WITH_VIDEO = "датасет-видео-тег.xlsx";//src/main/resources/датасет-видео-тег.xlsx
    public static final String PATH_FILE_WITH_AUDIO_EMBEDDING = "Mclip_transcription_embedding_3000-4000_2.xlsx";
    private final ExcelParser excelParser;
    private final CreateExcel createExcel;
    private final MediaContentService mediaContentService;
    private final ExcelDataMapper excelDataMapper;
    private final TagFrequencyCalculationService tagFrequencyCalculationService;

    @Autowired
    public ExcelDataServiceImpl(ExcelParser excelParser, CreateExcel createExcel, MediaContentService mediaContentService,
                                ExcelDataMapper excelDataMapper, TagFrequencyCalculationService tagFrequencyCalculationService) {
        this.excelParser = excelParser;
        this.createExcel = createExcel;
        this.mediaContentService = mediaContentService;
        this.excelDataMapper = excelDataMapper;
        this.tagFrequencyCalculationService = tagFrequencyCalculationService;
    }

    @Override
    public void importData() {
        List<VideoFromExcel> videoFromExcels;
        try {
            Resource resource = new ClassPathResource(PATH_FILE_WITH_VIDEO);
            if (resource.exists()) {
                try(InputStream inputStream = resource.getInputStream()) {
                    videoFromExcels = excelParser.parseMainExcelFile(inputStream);
                    Map<String, Integer> tagFrequency = tagFrequencyCalculationService.getMapTag(videoFromExcels);
                    List<MediaContent> mediaContents = excelDataMapper.buildMediaContentFromVideo(videoFromExcels, tagFrequency);

                    mediaContentService.saveAll(mediaContents);
                }
            } else {
                throw new FileNotFoundException("Файл не найден: " + PATH_FILE_WITH_VIDEO);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void importDataEmbedding() {
        List<EmbeddingAudio> embeddingAudios;
        try {
            Resource resource = new ClassPathResource(PATH_FILE_WITH_AUDIO_EMBEDDING);
            if (resource.exists()) {
                try(InputStream inputStream = resource.getInputStream()) {
                    embeddingAudios = excelParser.parseEmbeddingExcelFile(inputStream);
                    mediaContentService.updateAllTranscriptionsEmbedding(embeddingAudios);
                }
            } else {
                throw new FileNotFoundException("Файл не найден: " + PATH_FILE_WITH_VIDEO);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void createEmbeddingDataEmbedding() {
        List<VideoFromExcel> videoFromExcels;
        try {
            Resource resource = new ClassPathResource(PATH_FILE_WITH_AUDIO_EMBEDDING);
            if (resource.exists()) {
                try(InputStream inputStream = resource.getInputStream()) {
                    videoFromExcels = excelParser.parseMainExcelFile(inputStream);
                    createExcel.createEmbeddingFromUserDescription(videoFromExcels);
                }
            } else {
                throw new FileNotFoundException("Файл не найден: " + PATH_FILE_WITH_VIDEO);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
