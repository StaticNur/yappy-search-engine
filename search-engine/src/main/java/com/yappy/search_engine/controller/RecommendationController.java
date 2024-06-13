package com.yappy.search_engine.controller;

import com.yappy.search_engine.document.Video;
import com.yappy.search_engine.service.MediaContentService;
import com.yappy.search_engine.service.SearchService;
import com.yappy.search_engine.service.SuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RecommendationController {

    private final SearchService searchService;

    @Autowired
    public RecommendationController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/recommendations")
    public List<SearchHit<Video>> recommendations(@RequestParam String query,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "15") int size) {
        return searchService.searchVideosByText(query, page, size);
    }

}
