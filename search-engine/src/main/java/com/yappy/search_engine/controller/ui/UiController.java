package com.yappy.search_engine.controller.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class UiController {

    @GetMapping
    public String profileUi() {
        return "search";
    }
}
