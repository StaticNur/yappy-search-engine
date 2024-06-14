package com.yappy.search_engine.controller;

import com.yappy.search_engine.dto.VideoSearchResult;
import com.yappy.search_engine.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class RecommendationController {

    private final SearchService searchService;

    @Autowired
    public RecommendationController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/recommendations")
    public VideoSearchResult recommendations() {
        return searchService.getRecommendations();
    }
}
