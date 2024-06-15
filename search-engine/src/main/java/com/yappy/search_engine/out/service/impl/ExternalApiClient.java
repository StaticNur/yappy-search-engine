package com.yappy.search_engine.out.service.impl;

import com.yappy.search_engine.out.model.response.EmbeddingFromText;
import com.yappy.search_engine.out.model.response.TranscribedAudioResponse;
import com.yappy.search_engine.out.model.response.VisualDescription;
import com.yappy.search_engine.out.service.ApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Service
public class ExternalApiClient implements ApiClient {

    private final RestTemplate restTemplate;
    private final RetryTemplate retryTemplate;
    private final String transcriptionUrl;
    private final String visualDescriptionUrl;
    private final String embeddingUrl;

    @Autowired
    public ExternalApiClient(RestTemplate restTemplate, RetryTemplate retryTemplate,
                             @Value("${api.service.url.audio.transcription}") String transcriptionUrl,
                             @Value("${api.service.url.visual.description}") String visualDescriptionUrl,
                             @Value("${api.service.url.embedding}") String embeddingUrl) {
        this.restTemplate = restTemplate;
        this.retryTemplate = retryTemplate;
        this.transcriptionUrl = transcriptionUrl;
        this.visualDescriptionUrl = visualDescriptionUrl;
        this.embeddingUrl = embeddingUrl;
    }

    @Override
    public TranscribedAudioResponse getTranscription(String videoUrl) {
        String url = buildUrl(transcriptionUrl, "video_url", videoUrl);
        /*return retryTemplate.execute(context ->
                executePostRequest(url,
                        null,
                        TranscribedAudioResponse.class,
                        this::processTranscriptionResponse));*/
        return retryTemplate.execute(context -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

            ResponseEntity<Object[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    Object[].class
            );
            TranscribedAudioResponse transcription = new TranscribedAudioResponse();
            HttpStatus statusCode = response.getStatusCode();

            if (statusCode == HttpStatus.OK) {
                Object[] responseBody = response.getBody();
                if (responseBody != null && responseBody.length > 0) {
                    transcription.setText(responseBody[0] != null ? responseBody[0].toString() : "");

                    if (responseBody.length > 1 && responseBody[1] instanceof List<?> languagesList) {
                        if (!languagesList.isEmpty() && languagesList.get(0) instanceof List<?> firstLanguageEntry) {
                            if (!firstLanguageEntry.isEmpty()) {
                                transcription.setLanguages(firstLanguageEntry.get(0).toString());
                            }
                        }
                    }
                    System.out.println("Text: " + transcription.getText());
                    System.out.println("Languages: " + transcription.getLanguages());
                }
            } else {
                System.out.println("Error: " + statusCode);
            }
            return transcription;
        });
    }

    @Override
    public VisualDescription getVisualDescription(String videoUrl) {
        String url = buildUrl(visualDescriptionUrl, "video_url", videoUrl);
        return retryTemplate.execute(context ->
                executePostRequest(url,
                        null,
                        VisualDescription.class,
                        this::processVisualDescriptionResponse));
    }

    public double[] getEmbedding(String text) {
        String url = buildUrl(embeddingUrl, "text", text);
        return retryTemplate.execute(context -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Object> requestEntity = new HttpEntity<>(null, headers);

            ResponseEntity<EmbeddingFromText> response = restTemplate.exchange(url,
                    HttpMethod.POST, requestEntity, EmbeddingFromText.class);
            HttpStatus statusCode = response.getStatusCode();

            if (statusCode == HttpStatus.OK && response.getBody() != null) {
                return response.getBody().getResult();
            } else {
                System.out.println("Error: " + statusCode);
                return null;
            }
        });
    }

    /*@Override
    public TranscribedAudioResponse getTranscription(String videoUrl) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(transcriptionUrl)
                .queryParam("video_url", videoUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

        ResponseEntity<Object[]> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.POST,
                requestEntity,
                Object[].class
        );
        TranscribedAudioResponse transcription = new TranscribedAudioResponse();
        HttpStatus statusCode = response.getStatusCode();

        if (statusCode == HttpStatus.OK) {
            Object[] responseBody = response.getBody();
            if (responseBody != null && responseBody.length > 0) {
                transcription.setText(responseBody[0] != null ? responseBody[0].toString() : "");

                if (responseBody.length > 1 && responseBody[1] instanceof List<?> languagesList) {
                    if (!languagesList.isEmpty() && languagesList.get(0) instanceof List<?> firstLanguageEntry) {
                        if (!firstLanguageEntry.isEmpty()) {
                            transcription.setLanguages(firstLanguageEntry.get(0).toString());
                        }
                    }
                }
                System.out.println("Text: " + transcription.getText());
                System.out.println("Languages: " + transcription.getLanguages());
            }
        } else {
            System.out.println("Error: " + statusCode);
        }
        return transcription;
    }

    @Override
    public VisualDescription getVisualDescription(String videoUrl) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(visualDescriptionUrl)
                .queryParam("video_url", videoUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

        ResponseEntity<VisualDescription> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.POST,
                requestEntity,
                VisualDescription.class
        );
        VisualDescription visualDescription = new VisualDescription();
        HttpStatus statusCode = response.getStatusCode();

        if (statusCode == HttpStatus.OK) {
            VisualDescription responseBody = response.getBody();
            if (responseBody != null){
                System.out.println("VisualDescription: " + responseBody.getResult());
                System.out.println("isSuccess: " + responseBody.isSuccess());
            }
        } else {
            System.out.println("Error: " + statusCode);
        }
        return visualDescription;
    }*/
    private <T> T executePostRequest(String url, Object body, Class<T> responseType,
                                     Function<ResponseEntity<T>, T> responseHandler) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, responseType);
        HttpStatus statusCode = response.getStatusCode();

        if (statusCode == HttpStatus.OK) {
            return responseHandler.apply(response);
        } else {
            System.out.println("Error: " + statusCode);
            return null;
        }
    }

    private String buildUrl(String baseUrl, String paramName, String paramValue) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam(paramName, paramValue)
                .toUriString();
    }

    private TranscribedAudioResponse processTranscriptionResponse(ResponseEntity<TranscribedAudioResponse> response) {
        TranscribedAudioResponse transcription = response.getBody();
        if (transcription != null) {
            System.out.println("Text: " + transcription.getText());
            System.out.println("Languages: " + transcription.getLanguages());
        }
        return transcription;
    }

    private VisualDescription processVisualDescriptionResponse(ResponseEntity<VisualDescription> response) {
        VisualDescription visualDescription = response.getBody();
        if (visualDescription != null) {
            System.out.println("VisualDescription: " + visualDescription.getResult());
            System.out.println("isSuccess: " + visualDescription.isSuccess());
        }
        return visualDescription;
    }
}
