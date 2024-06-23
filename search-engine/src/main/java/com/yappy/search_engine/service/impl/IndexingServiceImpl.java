package com.yappy.search_engine.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yappy.search_engine.document.Video;
import com.yappy.search_engine.dto.VideoDto;
import com.yappy.search_engine.dto.VideoDtoFromInspectors;
import com.yappy.search_engine.mapper.MediaContentMapper;
import com.yappy.search_engine.mapper.VideoMapper;
import com.yappy.search_engine.model.MediaContent;
import com.yappy.search_engine.out.model.TranscribedAudioResponse;
import com.yappy.search_engine.out.model.VisualDescription;
import com.yappy.search_engine.out.service.ApiClient;
import com.yappy.search_engine.service.MediaContentService;
import com.yappy.search_engine.service.IndexingService;
import liquibase.repackaged.org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.xcontent.XContentBuilder;
import org.elasticsearch.xcontent.XContentFactory;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class IndexingServiceImpl implements IndexingService {

    private final static String INDEX_VIDEO_NAME = "videos";
    private final static String INDEX_AUTOCOMPLETE_NAME = "suggestions";
    private final static String FIELD_AUTOCOMPLETE = "suggestion";
    private static final double[] EMPTY_VECTOR;
    private final static int EMBEDDING_LENGTH = 640;

    private final RestHighLevelClient client;
    private final ObjectMapper objectMapper;
    private final MediaContentService mediaContentService;
    private final ApiClient apiClient;
    private final VideoMapper videoMapper;
    private final MediaContentMapper mediaContentMapper;

    @Autowired
    public IndexingServiceImpl(RestHighLevelClient client, ObjectMapper objectMapper,
                               MediaContentService mediaContentService, ApiClient apiClient,
                               VideoMapper videoMapper, MediaContentMapper mediaContentMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
        this.mediaContentService = mediaContentService;
        this.apiClient = apiClient;
        this.videoMapper = videoMapper;
        this.mediaContentMapper = mediaContentMapper;
    }

    @Override
    @Transactional
    public MediaContent indexVideo(VideoDto videoDto) {
        long begin = System.currentTimeMillis();
        MediaContent videoForPostgres = mediaContentMapper.buildVideoFromDto(videoDto);
        videoDataEnriched(videoForPostgres);
        try {
            Video videoForElastic = videoMapper.buildVideoFromMediaContent(videoForPostgres);
            mediaContentService.save(videoForPostgres);

            IndexRequest request = new IndexRequest(INDEX_VIDEO_NAME)
                    .id(videoForPostgres.getUuid().toString())
                    .source(objectMapper.writeValueAsString(videoForElastic), XContentType.JSON);

            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            System.out.println("Indexed video with ID: " + response.getId());

            long time = System.currentTimeMillis() - begin;
            mediaContentService.updateIndexingTime(videoForElastic.getUrl(), time);
            System.out.println("Indexed " + time + " video with ID: " + response.getId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return videoForPostgres;
    }

    @Override
    public void indexAllVideoFromDb() {
        long batchSize = 1_000;
        long fromIndex = 0;
        long toIndex;

        List<MediaContent> videoBatch = mediaContentService.getAllVideo();

        while (fromIndex < videoBatch.size()) {
            toIndex = Math.min(fromIndex + batchSize, videoBatch.size());

            List<MediaContent> batch = videoBatch.subList((int) fromIndex, (int) toIndex);

            BulkRequest bulkRequest = prepareBulkRequest(batch);
            executeBulkRequest(bulkRequest);
            fromIndex += batchSize;
            System.out.println("Проиндексировано " + fromIndex + " из БД в ElasticSearch");
        }
    }

    @Override
    public void indexAutocompleteDataFromDbInEs() {
        long batchSize = 10_000;
        long fromIndex = 0;
        long toIndex;

        List<MediaContent> videoBatch = mediaContentService.getAllVideo();
        Set<String> suggestions = getAllSuggestions(videoBatch);
        List<String> suggestionsList = new ArrayList<>(suggestions);

        while (fromIndex < suggestionsList.size()) {
            toIndex = Math.min(fromIndex + batchSize, suggestionsList.size());

            List<String> batch = suggestionsList.subList((int) fromIndex, (int) toIndex);

            BulkRequest bulkRequest = prepareBulkRequestAutocomplete(batch);
            executeBulkRequest(bulkRequest);
            fromIndex += batchSize;
        }
        System.out.println("Size suggestions: " + suggestionsList.size());
    }

    @Override
    public MediaContent indexVideoForInspectors(VideoDtoFromInspectors videoDto) {
        VideoDto video = videoMapper.buildVideoFromInspectorsDto(videoDto);
        return indexVideo(video);
    }

    private BulkRequest prepareBulkRequestAutocomplete(List<String> allSuggestions) {
        BulkRequest bulkRequest = new BulkRequest();
        for (String suggestion : allSuggestions) {
            try {
                XContentBuilder builder = XContentFactory.jsonBuilder();
                builder.startObject();
                builder.field(FIELD_AUTOCOMPLETE, suggestion);
                builder.endObject();

                IndexRequest indexRequest = new IndexRequest(INDEX_AUTOCOMPLETE_NAME)
                        .source(builder);

                bulkRequest.add(indexRequest);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return bulkRequest;
    }

    private Set<String> getAllSuggestions(List<MediaContent> videoBatch) {
        Set<String> suggestions = new HashSet<>();
        for (MediaContent mediaContent : videoBatch) {
            String descriptionUser = Objects.requireNonNullElse(mediaContent.getDescriptionUser(), "");
            if (StringUtils.isNotBlank(descriptionUser)) {
                suggestions.add(descriptionUser.trim().toLowerCase());
            }

            String tags = Objects.requireNonNullElse(mediaContent.getTags(), "");
            if (StringUtils.isNotBlank(tags)) {
                String[] arrayTags = StringUtils.split(tags, "#");
                for (String tag : arrayTags) {
                    if (StringUtils.isNotBlank(tag)) {
                        suggestions.add(tag.trim().toLowerCase());
                    }
                }
            }
        }
        return suggestions;
    }

    private BulkRequest prepareBulkRequest(List<MediaContent> allVideo) {
        BulkRequest bulkRequest = new BulkRequest();
        Video video;
        for (MediaContent mediaContent : allVideo) {
            IndexRequest indexRequest = new IndexRequest(INDEX_VIDEO_NAME);
            indexRequest.id(mediaContent.getUuid().toString());
            video = videoMapper.buildVideoFromMediaContent(mediaContent);
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

    private void videoDataEnriched(MediaContent videoForPostgres) {
        String url = videoForPostgres.getUrl();

        // Создаем асинхронные задачи для каждого API вызова
        CompletableFuture<TranscribedAudioResponse> transcriptionFuture = CompletableFuture.supplyAsync(
                () -> apiClient.getTranscription(url));
        CompletableFuture<VisualDescription> visualDescriptionFuture = CompletableFuture.supplyAsync(
                () -> apiClient.getVisualDescription(url));

        // Обработка результатов после выполнения задач
        try {
            TranscribedAudioResponse transcriptionAudio = transcriptionFuture.get();
            videoForPostgres.setTranscriptionAudio(transcriptionAudio.getText());
            videoForPostgres.setLanguageAudio(transcriptionAudio.getLanguages());

            VisualDescription visualDescription = visualDescriptionFuture.get();
            videoForPostgres.setDescriptionVisual(visualDescription.getResult());



            // Создаем асинхронные задачи для получения эмбеддингов
            CompletableFuture<double[]> embeddingAudioFuture = CompletableFuture.supplyAsync(
                    () -> {
                        String resultAudio = transcriptionAudio.getText();
                        if (resultAudio != null && !resultAudio.isBlank()) {
                            return apiClient.getEmbedding(resultAudio);
                        } else {
                            return EMPTY_VECTOR;
                        }
                    });

            CompletableFuture<double[]> embeddingVisualFuture = CompletableFuture.supplyAsync(
                    () -> {
                        String resultVisual = visualDescription.getResult();
                        if (resultVisual != null && !resultVisual.isBlank()) {
                            return apiClient.getEmbedding(resultVisual);
                        } else {
                            return EMPTY_VECTOR;
                        }
                    });

            CompletableFuture<double[]> embeddingUserDescriptionFuture = CompletableFuture.supplyAsync(
                    () -> {
                        String userAllDescription = videoForPostgres.getTitle()
                                                    + " " + videoForPostgres.getDescriptionUser()
                                                    + " " + videoForPostgres.getTags();
                        userAllDescription = userAllDescription.replaceAll("null", "").trim();
                        if (!userAllDescription.isBlank()) {
                            return apiClient.getEmbedding(userAllDescription);
                        } else {
                            return EMPTY_VECTOR;
                        }
                    });

            // Обработка результатов после выполнения задач
            double[] embeddingAudio = embeddingAudioFuture.get();
            videoForPostgres.setEmbeddingAudio(Arrays.toString(embeddingAudio));

            double[] embeddingVisual = embeddingVisualFuture.get();
            videoForPostgres.setEmbeddingVisual(Arrays.toString(embeddingVisual));

            double[] embeddingUserDescription = embeddingUserDescriptionFuture.get();
            videoForPostgres.setEmbeddingUserDescription(Arrays.toString(embeddingUserDescription));
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /*private void videoDataEnriched(MediaContent videoForPostgres) {
        String url = videoForPostgres.getUrl();
        TranscribedAudioResponse transcriptionAudio = apiClient.getTranscription(url);
        videoForPostgres.setTranscriptionAudio(transcriptionAudio.getText());
        videoForPostgres.setLanguageAudio(transcriptionAudio.getLanguages());

        VisualDescription visualDescription = apiClient.getVisualDescription(url);
        videoForPostgres.setDescriptionVisual(visualDescription.getResult());

        String resultAudio = transcriptionAudio.getText();
        double[] embeddingAudio = EMPTY_VECTOR;
        if (resultAudio != null && !resultAudio.isBlank()) {
            embeddingAudio = apiClient.getEmbedding(transcriptionAudio.getText());
        }
        videoForPostgres.setEmbeddingAudio(Arrays.toString(embeddingAudio));

        String resultVisual = visualDescription.getResult();
        double[] embeddingVisual = EMPTY_VECTOR;
        if (resultVisual != null && !resultVisual.isBlank()) {
            embeddingVisual = apiClient.getEmbedding(visualDescription.getResult());
        }
        videoForPostgres.setEmbeddingVisual(Arrays.toString(embeddingVisual));


        String userAllDescription = videoForPostgres.getTitle()
                                    + " " + videoForPostgres.getDescriptionUser()
                                    + " " + videoForPostgres.getTags();
        userAllDescription = userAllDescription.replaceAll("null", "");
        double[] embeddingUserDescription = EMPTY_VECTOR;
        if (!userAllDescription.isBlank()) {
            embeddingUserDescription = apiClient.getEmbedding(userAllDescription.trim());
        }
        videoForPostgres.setEmbeddingUserDescription(Arrays.toString(embeddingUserDescription));
    }*/

    static {
        EMPTY_VECTOR = new double[EMBEDDING_LENGTH]; //sb.toString();
        for (int i = 0; i < EMBEDDING_LENGTH; i++) {
            EMPTY_VECTOR[i] = 1.0;
        }
    }
}
