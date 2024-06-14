package com.yappy.search_engine.service;

import com.yappy.search_engine.document.Video;
import com.yappy.search_engine.dto.SearchByEmbeddingDto;
import com.yappy.search_engine.dto.SearchRequestDto;
import com.yappy.search_engine.dto.VideoSearchResult;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.Date;
import java.util.List;

public interface SearchService {
    SearchHits<Video> searchVideosByEmbedding(SearchByEmbeddingDto embedding, int page, int size);
    List<SearchHit<Video>> searchVideosByText(String query, int page, int size);
    VideoSearchResult searchVideoLexicographic(String query, int page, int size);

    VideoSearchResult getRecommendations();

    VideoSearchResult searchWithFilter(SearchRequestDto dto, String date);
}
