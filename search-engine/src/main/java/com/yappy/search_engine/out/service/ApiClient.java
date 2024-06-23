package com.yappy.search_engine.out.service;

import com.yappy.search_engine.out.model.TranscribedAudioResponse;
import com.yappy.search_engine.out.model.VisualDescription;

public interface ApiClient {

    TranscribedAudioResponse getTranscription(String videoUrl);

    VisualDescription getVisualDescription(String videoUrl);

    double[] getEmbedding(String text);

}
