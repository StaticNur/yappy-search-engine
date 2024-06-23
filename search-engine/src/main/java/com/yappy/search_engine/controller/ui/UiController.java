package com.yappy.search_engine.controller.ui;

import com.yappy.search_engine.dto.VideoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class UiController {

    @GetMapping
    public String searchPageUi() {
        return "search";
    }

    @PostMapping("/info-video")
    public ResponseEntity<String> handlePost(@RequestBody VideoResponse videoData, HttpSession session) throws IOException {
        String dataId = videoData.getUuid().toString();
        session.setAttribute(dataId, videoData);
        return ResponseEntity.ok("/info-video?dataId=" + dataId);
    }

    @GetMapping("/info-video")
    public String infoVideo(@RequestParam("dataId") String dataId, HttpSession session, Model model) {
        VideoResponse videoData = (VideoResponse) session.getAttribute(dataId);
        model.addAttribute("videoData", videoData);
        return "info-video";
    }

}
