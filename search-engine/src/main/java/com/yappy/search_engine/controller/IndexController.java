package com.yappy.search_engine.controller;

import com.yappy.search_engine.document.Video;
import com.yappy.search_engine.dto.Response;
import com.yappy.search_engine.dto.VideoDto;
import com.yappy.search_engine.service.VideoIndexService;
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

    private final VideoIndexService service;

    @Autowired
    public IndexController(VideoIndexService service) {
        this.service = service;
    }

    @PostMapping("/index")
    public ResponseEntity<Response> index(@RequestBody VideoDto videoDto) {
        service.indexVideo(videoDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new Response("Данные успешно индексированы в ElasticSearch"));
    }

    @PostMapping("/index-all")
    public ResponseEntity<Response> indexationDataInEs() {
        service.indexAllVideoFromDb();
        return ResponseEntity.status(HttpStatus.CREATED).body(new Response("Данные успешно загружены из PostgreSQL в ElasticSearch"));
    }
}
