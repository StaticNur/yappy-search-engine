package com.yappy.search_engine.service;

import com.yappy.search_engine.dto.VideoDto;
import com.yappy.search_engine.dto.VideoDtoFromInspectors;
import com.yappy.search_engine.model.MediaContent;

public interface IndexingService {
    MediaContent indexVideo(VideoDto videoDto);
    void indexAllVideoFromDb();

    void indexAutocompleteDataFromDbInEs();

    MediaContent indexVideoForInspectors(VideoDtoFromInspectors videoDto);

    void indexAutocompleteDataFromFile();
}
