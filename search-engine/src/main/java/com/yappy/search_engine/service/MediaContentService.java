package com.yappy.search_engine.service;

import com.yappy.search_engine.model.Embedding;
import com.yappy.search_engine.model.MediaContent;
import com.yappy.search_engine.model.TranscriptionAudio;

import java.util.List;

public interface MediaContentService {

    List<MediaContent> getAllVideo();

    void saveAll(List<MediaContent> mediaContents);

    void save(MediaContent video);

    void updateAllTranscriptions(List<TranscriptionAudio> transcriptionAudios);
    void updateAllTranscriptionsEmbedding(List<Embedding> embeddings);

    void updateAllVideoEmbedding(List<Embedding> embeddings);

    void updateAllUserDescriptionEmbedding(List<Embedding> embeddings);


    void updateIndexingTime(String url, Long time);
    String getIndexingTime(String uuid);
}
