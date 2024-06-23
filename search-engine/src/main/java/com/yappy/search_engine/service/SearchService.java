package com.yappy.search_engine.service;

import com.yappy.search_engine.dto.SearchByParameterDto;
import com.yappy.search_engine.dto.SearchRequestDto;
import com.yappy.search_engine.dto.VideoSearchResult;

public interface SearchService {
    VideoSearchResult searchVideosByCombine(SearchByParameterDto embedding, int page, int size, String date);

    VideoSearchResult searchVideosByEmbedding(SearchByParameterDto embedding, int page, int size);

    VideoSearchResult searchVideoFullText(SearchByParameterDto embedding, int page, int size);

    VideoSearchResult searchVideoText(String query, int page, int size);

    VideoSearchResult getRecommendations();
    VideoSearchResult searchWithFilter(SearchRequestDto dto, String date);
}
