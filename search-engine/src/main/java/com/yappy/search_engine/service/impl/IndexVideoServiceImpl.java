package com.yappy.search_engine.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yappy.search_engine.document.Video;
import com.yappy.search_engine.dto.VideoDto;
import com.yappy.search_engine.model.MediaContent;
import com.yappy.search_engine.service.EmbeddingService;
import com.yappy.search_engine.service.MediaContentService;
import com.yappy.search_engine.service.VideoIndexService;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class IndexVideoServiceImpl implements VideoIndexService {

    private final static String INDEX_NAME = "videos";
    private final static int EMBEDDING_LENGTH = 768;

    private final RestHighLevelClient client;
    private final ObjectMapper objectMapper;
    private final MediaContentService mediaContentService;
    private final EmbeddingService embeddingService;

    @Autowired
    public IndexVideoServiceImpl(RestHighLevelClient client, ObjectMapper objectMapper,
                                 MediaContentService mediaContentService, EmbeddingService embeddingService) {
        this.client = client;
        this.objectMapper = objectMapper;
        this.mediaContentService = mediaContentService;
        this.embeddingService = embeddingService;
    }

    @Override
    @Transactional
    public void indexVideo(VideoDto videoDto) {
        MediaContent video = buildVideoFromDto(videoDto);
        try {
            Video video2 = buildVideoFromMediaContent(video);
            mediaContentService.save(video);

            IndexRequest request = new IndexRequest(INDEX_NAME)
                    .id(video.getUuid().toString())
                    .source(objectMapper.writeValueAsString(video2), XContentType.JSON);
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            System.out.println("Indexed video with ID: " + response.getId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void indexAllVideoFromDb() {
        long batchSize = 10_000;
        long fromIndex = 0;
        long toIndex;

        List<MediaContent> videoBatch = mediaContentService.getAllVideo();

        while (fromIndex < videoBatch.size()) {
            toIndex = Math.min(fromIndex + batchSize, videoBatch.size());

            List<MediaContent> batch = videoBatch.subList((int) fromIndex, (int) toIndex);

            BulkRequest bulkRequest = prepareBulkRequest(batch);
            executeBulkRequest(bulkRequest);
            fromIndex += batchSize;
        }
    }

    private BulkRequest prepareBulkRequest(List<MediaContent> allVideo) {
        BulkRequest bulkRequest = new BulkRequest();
        Video video;
        for (MediaContent mediaContent : allVideo) {
            IndexRequest indexRequest = new IndexRequest(INDEX_NAME);
            indexRequest.id(mediaContent.getUuid().toString());
            video = buildVideoFromMediaContent(mediaContent);
            try {
                indexRequest.source(objectMapper.writeValueAsString(video), XContentType.JSON);
                bulkRequest.add(indexRequest);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return bulkRequest;
    }

    private void executeBulkRequest(BulkRequest bulkRequest) {
        try {
            BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
            handleBulkResponse(bulkResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleBulkResponse(BulkResponse bulkResponse) {
        if (bulkResponse.hasFailures()) {
            for (BulkItemResponse bulkItemResponse : bulkResponse) {
                if (bulkItemResponse.isFailed()) {
                    BulkItemResponse.Failure failure = bulkItemResponse.getFailure();
                    throw new RuntimeException("Failed to process request: " + failure.getMessage());
                }
            }
        }
    }

    private Video buildVideoFromMediaContent(MediaContent mediaContent) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String formattedDate = (mediaContent.getCreated()).format(formatter);
        Integer popularity = mediaContent.getPopularity();
        if (popularity == null) {
            popularity = 0;
        }
        return new Video(
                mediaContent.getUuid().toString(),
                mediaContent.getUrl(),
                mediaContent.getTitle(),
                mediaContent.getDescriptionUser(),
                mediaContent.getDescriptionMl(),
                mediaContent.getTags(),
                formattedDate,
                popularity.toString(),
                convertToFloatArray(mediaContent.getEmbeddingAudio()),
                convertToFloatArray(mediaContent.getEmbeddingVisual()),
                convertToFloatArray(mediaContent.getEmbeddingUserDescription()),
                convertToFloatArray(mediaContent.getEmbeddingMlDescription())
        );
    }

    private MediaContent buildVideoFromDto(VideoDto videoDto) {
        MediaContent video = new MediaContent(UUID.randomUUID(),
                videoDto.getUrl(),
                videoDto.getTitle(),
                videoDto.getDescription(),
                videoDto.getTags(),
                LocalDateTime.now());
        embeddingService.getEmbeddingFromAudio(videoDto.getUrl());
        String empty = "[0.0]";
        video.setEmbeddingAudio(empty);

        embeddingService.getEmbeddingFromVisual(videoDto.getUrl());
        video.setEmbeddingVisual(empty);

        embeddingService.getEmbeddingFromUserDescription(videoDto.getUrl());
        video.setEmbeddingUserDescription(empty);

        embeddingService.getEmbeddingFromMlDescription(videoDto.getUrl());
        video.setEmbeddingMlDescription(empty);
        return video;
    }

    private double[] convertToFloatArray(String input) {
        input = input.replaceAll("[\\[\\]]", "");
        String[] parts = input.split(",");
        double[] result = new double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Double.parseDouble(parts[i].trim());
        }
        return result;
    }
}
