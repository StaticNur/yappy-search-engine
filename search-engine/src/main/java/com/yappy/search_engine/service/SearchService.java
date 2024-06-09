package com.yappy.search_engine.service;

import com.yappy.search_engine.document.Video;
import com.yappy.search_engine.dto.SearchByEmbeddingDto;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.List;

public interface SearchService {
    SearchHits<Video> searchVideosByEmbedding(SearchByEmbeddingDto embedding, int page, int size);
    List<SearchHit<Video>> searchVideosByText(String query, int page, int size);
    List<Video> searchVideoLexicographic(String query, int page, int size);
}
