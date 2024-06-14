package com.yappy.search_engine.controller;

import com.yappy.search_engine.document.Video;
import com.yappy.search_engine.dto.SearchByEmbeddingDto;
import com.yappy.search_engine.dto.VideoSearchResult;
import com.yappy.search_engine.service.MediaContentService;
import com.yappy.search_engine.service.SearchService;
import com.yappy.search_engine.service.SuggestionService;
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
    private final SuggestionService suggestionService;
    private final MediaContentService mediaContentService;

    @Autowired
    public SearchController(SearchService searchService, SuggestionService suggestionService,
                            MediaContentService mediaContentService) {
        this.searchService = searchService;
        this.suggestionService = suggestionService;
        this.mediaContentService = mediaContentService;
    }

    @GetMapping("/search/text")
    public List<SearchHit<Video>> searchByText(@RequestParam String query,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "15") int size) {
        return searchService.searchVideosByText(query, page, size);
    }

    @GetMapping("/search/text/lexicographic")
    public VideoSearchResult searchVideoLexicographic(@RequestParam String query,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "15") int size) {
        return searchService.searchVideoLexicographic(query.trim(), page, size);
    }

    @GetMapping("/search/embedding")
    public SearchHits<Video> searchByEmbedding(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "15") int size,
                                               @RequestBody SearchByEmbeddingDto embedding) {
        return searchService.searchVideosByEmbedding(embedding, page, size);
    }

    @GetMapping("/search/autocomplete")
    public List<String> autocomplete(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size,
                                     @RequestParam String query) {
        return suggestionService.getAutocomplete(query, page, size);
    }

    @GetMapping("/indexing-time")
    public String indexingTime(@RequestParam String uuid) {
        return mediaContentService.getIndexingTime(uuid);
    }
}
