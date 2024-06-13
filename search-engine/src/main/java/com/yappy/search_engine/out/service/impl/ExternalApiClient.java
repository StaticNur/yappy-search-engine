package com.yappy.search_engine.out.service.impl;

import com.yappy.search_engine.out.model.response.TranscribedAudioResponse;
import com.yappy.search_engine.out.service.ApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class ExternalApiClient implements ApiClient {

    private final RestTemplate restTemplate;

    @Value("${api.service.audio.url.transcription}")
    private String transcriptionUrl;

    @Autowired
    public ExternalApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
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
    public void getEmbeddingFromTranscription(String transcription) {

    }

}
