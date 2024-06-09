package com.yappy.search_engine.service;

public interface EmbeddingService {
    void getEmbeddingFromAudio(String url);

    void getEmbeddingFromVisual(String url);

    void getEmbeddingFromUserDescription(String url);

    void getEmbeddingFromMlDescription(String url);
}
