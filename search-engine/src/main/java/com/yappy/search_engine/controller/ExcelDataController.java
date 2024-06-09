package com.yappy.search_engine.controller;

import com.yappy.search_engine.dto.Response;
import com.yappy.search_engine.service.ExcelDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExcelDataController {

    private final ExcelDataService excelDataService;

    @Autowired
    public ExcelDataController(ExcelDataService excelDataService) {
        this.excelDataService = excelDataService;
    }

    @PostMapping("/import/excel")
    public ResponseEntity<Response> importDataFromExcel() {
        excelDataService.importData();
        return ResponseEntity.ok().body(new Response("Data imported successfully!"));
    }
}
