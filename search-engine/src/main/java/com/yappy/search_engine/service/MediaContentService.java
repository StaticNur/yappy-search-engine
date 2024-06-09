package com.yappy.search_engine.service;

import com.yappy.search_engine.document.Video;
import com.yappy.search_engine.model.MediaContent;
import com.yappy.search_engine.model.VideoFromExcel;

import java.util.List;

public interface MediaContentService {

    List<MediaContent> getAllVideo();

    void saveAll(List<VideoFromExcel> videoFromExcels);

    void save(MediaContent video);
}
