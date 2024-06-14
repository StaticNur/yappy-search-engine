package com.yappy.search_engine.service;

import com.yappy.search_engine.model.MediaContent;
import com.yappy.search_engine.model.TranscriptionAudio;
import com.yappy.search_engine.model.VideoFromExcel;

import java.util.List;

public interface MediaContentService {

    List<MediaContent> getAllVideo();

    void saveAll(List<MediaContent> mediaContents);

    void save(MediaContent video);

    void updateAllTranscriptions(List<TranscriptionAudio> transcriptionAudios);

    void updateIndexingTime(String url, Long time);

    String getIndexingTime(String uuid);
}
