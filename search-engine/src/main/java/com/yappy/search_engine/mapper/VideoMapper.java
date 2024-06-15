package com.yappy.search_engine.mapper;

import com.yappy.search_engine.document.Video;
import com.yappy.search_engine.model.MediaContent;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class VideoMapper {
    private static final double[] EMPTY_VECTOR;
    private final static int EMBEDDING_LENGTH = 640;

    public Video buildVideoFromMediaContent(MediaContent mediaContent)  {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String formattedDate = (mediaContent.getCreated()).format(formatter);
        Integer popularity = mediaContent.getPopularity();
        if (popularity == null) {
            popularity = 0;
        }
        double[] embeddingAudio = convertToFloatArray(mediaContent.getEmbeddingAudio());
        if(embeddingAudio.length < 10){
            embeddingAudio = EMPTY_VECTOR;
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
                embeddingAudio,
                EMPTY_VECTOR,
                EMPTY_VECTOR
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

    static {
        /*StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < EMBEDDING_LENGTH; i++) {
            sb.append("0.0");
            if (i < EMBEDDING_LENGTH - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");*/
        EMPTY_VECTOR = new double[EMBEDDING_LENGTH]; //sb.toString();
    }
}
