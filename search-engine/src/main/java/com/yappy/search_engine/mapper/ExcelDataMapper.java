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
            if (text.matches("^(\\s*,\\s*)*$")) {
                text = "";
            }
            mediaContent.setDescriptionUser(text);
            mediaContent.setTags(tagsBuilder.toString().trim());

            mediaContent.setTitle("");
            mediaContent.setCreated(LocalDateTime.now());

            String empty = "[0.0]";
            mediaContent.setEmbeddingAudio(empty);
            mediaContent.setEmbeddingVisual(empty);
            mediaContent.setEmbeddingUserDescription(empty);
            mediaContent.setEmbeddingMlDescription(empty);


            mediaContents.add(mediaContent);
        }
        return mediaContents;
    }
}
