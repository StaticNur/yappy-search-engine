package com.yappy.search_engine.out.service;

import com.yappy.search_engine.out.model.response.TranscribedAudioResponse;

public interface ApiClient {

    TranscribedAudioResponse getTranscription(String videoUrl);

    void getEmbeddingFromTranscription(String transcription);

}
