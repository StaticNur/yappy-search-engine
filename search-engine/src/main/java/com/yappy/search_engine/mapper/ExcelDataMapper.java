package com.yappy.search_engine.mapper;

import com.yappy.search_engine.model.MediaContent;
import com.yappy.search_engine.model.VideoFromExcel;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ExcelDataMapper {

    public List<MediaContent> buildMediaContentFromVideo(List<VideoFromExcel> videoFromExcels) {
        List<MediaContent> mediaContents = new ArrayList<>();
        for (VideoFromExcel videoFromExcel : videoFromExcels) {
            MediaContent mediaContent = new MediaContent();

            mediaContent.setUuid(UUID.randomUUID());
            mediaContent.setUrl(videoFromExcel.getUrl());
            mediaContent.setTitle("");

            StringBuilder descriptionUser = new StringBuilder();
            StringBuilder tagsBuilder = new StringBuilder();
            String[] parts = videoFromExcel.getDescription().split("\\s+");
            for (String part : parts) {
                if (part.startsWith("#")) {
                    tagsBuilder.append(part).append(" ");
                } else {
                    descriptionUser.append(" ").append(part);
                }
            }

            String text = descriptionUser.toString();
            if (text.matches("^(\\s*,\\s*)*$")) {// только запятые
                text = "";
            }
            mediaContent.setDescriptionUser(text.trim());
            mediaContent.setTranscriptionAudio("");
            mediaContent.setLanguageAudio("");
            mediaContent.setDescriptionVisual("");
            mediaContent.setTags(tagsBuilder.toString().trim());

            mediaContent.setCreated(LocalDateTime.now());
            mediaContent.setPopularity(0);
            mediaContent.setHash("");

            String empty = "[0.0]";
            mediaContent.setEmbeddingAudio(empty);
            mediaContent.setEmbeddingVisual(empty);
            mediaContent.setEmbeddingUserDescription(empty);
            mediaContent.setIndexingTime(0L);

            mediaContents.add(mediaContent);
        }
        return mediaContents;
    }
}
