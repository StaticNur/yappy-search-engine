package com.yappy.search_engine.controller;

import com.yappy.search_engine.dto.Response;
import com.yappy.search_engine.service.ImportExcelService;
import com.yappy.search_engine.service.ImportJsonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MigrationController {

    private final ImportExcelService excelDataService;
    private final ImportJsonService jsonDataService;

    @Autowired
    public MigrationController(ImportExcelService excelDataService, ImportJsonService jsonDataService) {
        this.excelDataService = excelDataService;
        this.jsonDataService = jsonDataService;
    }

    @PostMapping("/import/excel")
    public ResponseEntity<Response> importDataFromExcel() {
        excelDataService.importData();
        return ResponseEntity.ok().body(new Response("Data imported successfully!"));
    }

    @PostMapping("/import/transcription-audio")
    public ResponseEntity<Response> importDataFromJson() {
        jsonDataService.importData();
        return ResponseEntity.ok().body(new Response("Data imported successfully!"));
    }

    @PostMapping("/import/transcription-audio-high-quality")
    public ResponseEntity<Response> importAudioHighQualityFromExcel() {
        jsonDataService.importDataHighQuality();
        return ResponseEntity.ok().body(new Response("Data imported successfully!"));
    }

    @PostMapping("/import/transcription-embedding")
    public ResponseEntity<Response> importAudioFromExcel() {
        excelDataService.importAudioEmbedding();
        return ResponseEntity.ok().body(new Response("Data imported successfully!"));
    }

    @PostMapping("/import/transcription-embedding-classification")
    public ResponseEntity<Response> importAudioClassificationFromExcel() {
        excelDataService.importAudioClassificationEmbedding();
        return ResponseEntity.ok().body(new Response("Data imported successfully!"));
    }

    @PostMapping("/import/video-embedding")
    public ResponseEntity<Response> importVideoFromExcel() {
        excelDataService.importVideoEmbedding();
        return ResponseEntity.ok().body(new Response("Data imported successfully!"));
    }

    @PostMapping("/import/user-description-embedding")
    public ResponseEntity<Response> importUserDescriptionFromExcel() {
        excelDataService.importUserDescriptionEmbedding();
        return ResponseEntity.ok().body(new Response("Data imported successfully!"));
    }
}
