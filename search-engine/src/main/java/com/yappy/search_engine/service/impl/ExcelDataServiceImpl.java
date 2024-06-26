package com.yappy.search_engine.service.impl;

import com.yappy.search_engine.mapper.ExcelDataMapper;
import com.yappy.search_engine.model.Embedding;
import com.yappy.search_engine.model.MediaContent;
import com.yappy.search_engine.model.VideoFromExcel;
import com.yappy.search_engine.service.ImportExcelService;
import com.yappy.search_engine.service.MediaContentService;
import com.yappy.search_engine.util.TagFrequencyCalculation;
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
    public static final String PATH_FILE_WITH_VIDEO = "датасет-видео-тег.xlsx";
    public static final String PATH_FILE_WITH_AUDIO_4_10000_EMBEDDING = "Mclip_transcription_embedding_3000-4000_2.xlsx";
    public static final String PATH_FILE_WITH_AUDIO_1_10000_EMBEDDING = "Mclip_from_audio_0-11000.xlsx";
    public static final String PATH_FILE_WITH_AUDIO_CLASSIFICATION_1_10000_EMBEDDING = "MClip_audio_0_10700_new.xlsx";
    public static final String PATH_FILE_WITH_AUDIO_CLASSIFICATION_4_10000_EMBEDDING = "MClip_audio_30000_40000.xlsx";
    public static final String PATH_FILE_WITH_VIDEO_EMBEDDING = "MCLIP_video_0_10700.xlsx";
    public static final String PATH_FILE_WITH_USER_DESCRIPTION_EMBEDDING = "Mclip_tags_11000.xlsx";
    private final ExcelParser excelParser;
    private final MediaContentService mediaContentService;
    private final ExcelDataMapper excelDataMapper;

    @Autowired
    public ExcelDataServiceImpl(ExcelParser excelParser, MediaContentService mediaContentService,
                                ExcelDataMapper excelDataMapper) {
        this.excelParser = excelParser;
        this.mediaContentService = mediaContentService;
        this.excelDataMapper = excelDataMapper;
    }

    @Override
    public void importData() {
        List<VideoFromExcel> videoFromExcels;
        try {
            Resource resource = new ClassPathResource(PATH_FILE_WITH_VIDEO);
            if (resource.exists()) {
                try (InputStream inputStream = resource.getInputStream()) {
                    videoFromExcels = excelParser.parseMainExcelFile(inputStream);
                    Map<String, Integer> tagFrequency = TagFrequencyCalculation.getMapTag(videoFromExcels);
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
    public void importAudioEmbedding() {
        try {
            boolean isUserDescription = false;
            boolean removeBrackets = true;//для удаления лишних квадратных скобок
            downloadAudio(PATH_FILE_WITH_AUDIO_1_10000_EMBEDDING, isUserDescription, removeBrackets);
            downloadAudio(PATH_FILE_WITH_AUDIO_4_10000_EMBEDDING, isUserDescription, removeBrackets);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void importVideoEmbedding() {
        try {
            downloadVisual(PATH_FILE_WITH_VIDEO_EMBEDDING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void importUserDescriptionEmbedding() {
        try {
            boolean isUserDescription = true;
            boolean removeBrackets = true;//для удаления лишних квадратных скобок
            downloadAudio(PATH_FILE_WITH_USER_DESCRIPTION_EMBEDDING, isUserDescription, removeBrackets);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void importAudioClassificationEmbedding() {
        try {
            boolean isUserDescription = false;
            boolean removeBrackets = false;
            downloadAudio(PATH_FILE_WITH_AUDIO_CLASSIFICATION_1_10000_EMBEDDING, isUserDescription, removeBrackets);
            downloadAudio(PATH_FILE_WITH_AUDIO_CLASSIFICATION_4_10000_EMBEDDING, isUserDescription, removeBrackets);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void downloadAudio(String fileName, boolean isUserDescription, boolean removeBrackets) throws IOException {
        List<Embedding> embeddings;
        Resource resource = new ClassPathResource(fileName);
        if (resource.exists()) {
            try (InputStream inputStream = resource.getInputStream()) {
                embeddings = excelParser.parseEmbeddingExcelFile(inputStream, removeBrackets);
                if (isUserDescription) {
                    mediaContentService.updateAllUserDescriptionEmbedding(embeddings);
                } else {
                    mediaContentService.updateAllTranscriptionsEmbedding(embeddings);
                }
            }
        } else {
            throw new FileNotFoundException("Файл не найден: " + fileName);
        }
    }

    private void downloadVisual(String fileName) throws IOException {
        boolean removeBrackets = false;
        List<Embedding> embeddings;
        Resource resource = new ClassPathResource(fileName);
        if (resource.exists()) {
            try (InputStream inputStream = resource.getInputStream()) {
                embeddings = excelParser.parseEmbeddingExcelFile(inputStream, removeBrackets);
                mediaContentService.updateAllVideoEmbedding(embeddings);
            }
        } else {
            throw new FileNotFoundException("Файл не найден: " + fileName);
        }
    }
}
