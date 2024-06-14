package com.yappy.search_engine.controller;

import com.yappy.search_engine.dto.Response;
import com.yappy.search_engine.service.ImportExcelService;
import com.yappy.search_engine.service.ImportJsonService;
import com.yappy.search_engine.service.impl.ExcelDataServiceImpl;
import com.yappy.search_engine.service.impl.JsonDataServiceImpl;
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

    @PostMapping("/import/popularity")
    public ResponseEntity<Response> importTagsFromExcel() {
        excelDataService.importDataPopularity();
        return ResponseEntity.ok().body(new Response("Data imported successfully!"));
    }
}
