package com.yappy.search_engine.controller;

import com.yappy.search_engine.document.Video;
import com.yappy.search_engine.dto.SearchRequestDto;
import com.yappy.search_engine.dto.VideoSearchResult;
import com.yappy.search_engine.service.MediaContentService;
import com.yappy.search_engine.service.SearchService;
import com.yappy.search_engine.service.SuggestionService;
import org.apache.poi.ss.util.DateParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@RestController
public class RecommendationController {

    private final SearchService searchService;

    @Autowired
    public RecommendationController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/recommendations")
    public VideoSearchResult recommendations(@RequestParam
                                                 @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                      final Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String formattedDate = formatter.format(date);

        return searchService.getVideoByDate(formattedDate);
    }

    @PostMapping("/search")
    public VideoSearchResult searchCreatedSince(
            @RequestBody final SearchRequestDto dto,
            @RequestParam(defaultValue = "1971-01-01")
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            final Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String formattedDate = formatter.format(date);

        return searchService.search(dto, formattedDate);
    }

}
