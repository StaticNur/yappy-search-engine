package com.yappy.search_engine.mapper;

import com.yappy.search_engine.model.MediaContent;
import com.yappy.search_engine.model.VideoFromExcel;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ExcelDataMapper {

    /*StringBuilder descriptionUser = new StringBuilder();
        StringBuilder tagsBuilder = new StringBuilder();
        String[] parts = videoFromExcel.getDescription().split("\\s+");
        for (String part : parts) {
            if (part.startsWith("#")) {
                tagsBuilder.append(part).append(" ");
            } else {
                descriptionUser.append(" ").append(part);
            }
        }*/

    public List<MediaContent> buildMediaContentFromVideo(List<VideoFromExcel> videoFromExcels,
                                                         Map<String, Integer> tagFrequency) {
        List<MediaContent> mediaContents = new ArrayList<>();
        for (VideoFromExcel videoFromExcel : videoFromExcels) {
            MediaContent mediaContent = new MediaContent();

            mediaContent.setUuid(UUID.randomUUID());
            mediaContent.setUrl(videoFromExcel.getUrl());
            mediaContent.setTitle("");

            StringBuilder descriptionUser = new StringBuilder();
            StringBuilder tagsBuilder = new StringBuilder();
            Pattern tagPattern = Pattern.compile("#[\\wа-яё]+", Pattern.CASE_INSENSITIVE);
            Matcher matcher = tagPattern.matcher(videoFromExcel.getDescription());
            int lastIndex = 0;
            while (matcher.find()) {
                descriptionUser.append(videoFromExcel.getDescription(), lastIndex, matcher.start());
                tagsBuilder.append(matcher.group()).append(" ");
                lastIndex = matcher.end();
            }
            descriptionUser.append(videoFromExcel.getDescription().substring(lastIndex));

            String text = descriptionUser.toString().trim();
            if (text.matches("^(\\s*,\\s*)*$")) {// только запятые
                text = "";
            }
            text = text.replaceAll("[\\s]+", " ");

            mediaContent.setDescriptionUser(text);
            mediaContent.setTranscriptionAudio("");
            mediaContent.setLanguageAudio("");
            mediaContent.setDescriptionVisual("");

            String tags = tagsBuilder.toString().trim();
            mediaContent.setTags(tags);
            mediaContent.setCreated(LocalDateTime.now());
            int maxPopularity = Arrays.stream(tags.split("\\s+"))
                    .map(tagFrequency::get)
                    .filter(Objects::nonNull)
                    .max(Integer::compare)
                    .orElse(0);
            mediaContent.setPopularity(maxPopularity);

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
