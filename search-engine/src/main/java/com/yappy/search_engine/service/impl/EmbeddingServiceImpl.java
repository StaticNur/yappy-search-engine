package com.yappy.search_engine.service.impl;

import com.yappy.search_engine.service.EmbeddingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EmbeddingServiceImpl implements EmbeddingService {

    private final RestTemplate restTemplate;

    @Value("${embedding.service.url.audio}")
    private String audioUrl;

    @Value("${embedding.service.url.visual}")
    private String visualUrl;

    @Value("${embedding.service.url.user_description}")
    private String userDescriptionUrl;

    @Value("${embedding.service.url.ml_description}")
    private String mlDescriptionUrl;

    @Autowired
    public EmbeddingServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void getEmbeddingFromAudio(String url) {
        //restTemplate.getForObject(url, EmbeddingAudio.class);
    }

    @Override
    public void getEmbeddingFromVisual(String url) {
        //restTemplate.getForObject(url, EmbeddingVisual.class);
    }

    @Override
    public void getEmbeddingFromUserDescription(String url) {
        //restTemplate.getForObject(url, EmbeddingVisual.class);
    }

    @Override
    public void getEmbeddingFromMlDescription(String url) {
        //restTemplate.getForObject(url, EmbeddingVisual.class);
    }
}
