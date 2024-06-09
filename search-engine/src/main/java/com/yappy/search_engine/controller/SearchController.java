package com.yappy.search_engine.controller;

import com.yappy.search_engine.document.Video;
import com.yappy.search_engine.dto.SearchByEmbeddingDto;
import com.yappy.search_engine.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search/text")
    public List<SearchHit<Video>> searchByText(@RequestParam String query,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "15") int size) {
        return searchService.searchVideosByText(query, page, size);
    }

    @GetMapping("/search/text/lexicographic")
    public List<Video> searchVideoLexicographic(@RequestParam String query,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "15") int size) {
        return searchService.searchVideoLexicographic(query, page, size);
    }

    @GetMapping("/search/embedding")
    public SearchHits<Video> searchByEmbedding(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "15") int size,
                                               @RequestBody SearchByEmbeddingDto embedding) {
        return searchService.searchVideosByEmbedding(embedding, page, size);
    }
}
