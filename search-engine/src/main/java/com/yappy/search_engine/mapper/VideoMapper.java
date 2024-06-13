package com.yappy.search_engine.mapper;

import com.yappy.search_engine.document.Video;
import com.yappy.search_engine.model.MediaContent;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class VideoMapper {

    public Video buildVideoFromMediaContent(MediaContent mediaContent)  {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String formattedDate = (mediaContent.getCreated()).format(formatter);
        Integer popularity = mediaContent.getPopularity();
        if (popularity == null) {
            popularity = 0;
        }
        return new Video(
                mediaContent.getUuid().toString(),
                mediaContent.getUrl(),
                mediaContent.getTitle(),
                mediaContent.getDescriptionUser(),
                mediaContent.getTranscriptionAudio(),
                mediaContent.getLanguageAudio(),
                mediaContent.getDescriptionVisual(),
                mediaContent.getTags(),
                formattedDate,
                popularity.toString(),
                mediaContent.getHash(),
                convertToFloatArray(mediaContent.getEmbeddingAudio()),
                convertToFloatArray(mediaContent.getEmbeddingVisual()),
                convertToFloatArray(mediaContent.getEmbeddingUserDescription())
        );
    }

    private double[] convertToFloatArray(String input) {
        input = input.replaceAll("[\\[\\]]", "");
        String[] parts = input.split(",");
        double[] result = new double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Double.parseDouble(parts[i].trim());
        }
        return result;
    }
}
