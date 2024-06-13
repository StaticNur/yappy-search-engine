package com.yappy.search_engine.controller;

import com.yappy.search_engine.dto.Response;
import com.yappy.search_engine.dto.VideoDto;
import com.yappy.search_engine.model.MediaContent;
import com.yappy.search_engine.service.IndexingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class IndexController {

    private final IndexingService service;

    @Autowired
    public IndexController(IndexingService service) {
        this.service = service;
    }

    @PostMapping("/index")
    public ResponseEntity<MediaContent> index(@RequestBody VideoDto videoDto) {
        MediaContent mediaContent = service.indexVideo(videoDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mediaContent);
    }

    @PostMapping("/index-all")
    public ResponseEntity<Response> indexationDataInEs() {
        service.indexAllVideoFromDb();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Response("Данные успешно загружены из PostgreSQL в ElasticSearch"));
    }

    @PostMapping("/index/autocomplete")
    public ResponseEntity<Response> indexationAutocompleteDataFromDbInEs() {
        service.indexAutocompleteDataFromDbInEs();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Response("Данные autocomplete успешно загружены из PostgreSQL в ElasticSearch"));
    }
}
