package com.yappy.reading_audio.controller;

import com.yappy.reading_audio.service.AudioExtractorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AudioController {

    @Autowired
    private AudioExtractorService audioExtractorService;

    @PostMapping("/extract-audio")
    public String extractAndSaveAudio(@RequestBody String videoUrl) {
        try {
            audioExtractorService.extractAndSaveAudio(videoUrl, "output_audio.mp3");
            return "Аудио успешно извлечено и сохранено!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Ошибка при извлечении и сохранении аудио: " + e.getMessage();
        }
    }
}

