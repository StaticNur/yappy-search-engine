package com.yappy.search_engine.controller.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping
public class UiController {

    @GetMapping
    public String searchPageUi() {
        return "search";
    }

    @GetMapping("/info-video.html")
    public String infoPageUi(@RequestParam(name = "data", required = false) String data) {
        return "info-video";
    }
}
