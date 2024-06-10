package com.yappy.search_engine.service;

import com.yappy.search_engine.dto.VideoDto;

public interface IndexingService {
    void indexVideo(VideoDto videoDto);
    void indexAllVideoFromDb();

    void indexAutocompleteDataFromDbInEs();
}
