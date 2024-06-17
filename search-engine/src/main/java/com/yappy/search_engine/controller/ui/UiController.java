package com.yappy.search_engine.controller.ui;

import com.yappy.search_engine.document.Video;
import com.yappy.search_engine.model.MediaContent;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@Controller
public class UiController {

    @GetMapping
    public String searchPageUi() {
        return "search";
    }

    @PostMapping("/info-video")
    @ResponseBody
    public String handlePost(@RequestBody Video videoData, HttpSession session) throws IOException {
        String dataId = videoData.getUuid().toString();
        session.setAttribute(dataId, videoData);

        return "/info-video?dataId=" + dataId; // Return only the path
    }

    @GetMapping("/info-video")
    public String infoPageUi(@RequestParam(name = "dataId", required = false) String dataId,
                             HttpSession session, Model model) {
        if (dataId != null) {
            Video videoData = (Video) session.getAttribute(dataId);
            model.addAttribute("videoData", videoData);
        }
        return "info-video";
    }

}
