package com.yappy.search_engine.mapper;

import com.yappy.search_engine.document.Video;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SearchHitMapper {

    public Video getVideo(org.elasticsearch.search.SearchHit hit) {
        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
        return new Video(
                getStringValue(sourceAsMap, "uuid"),
                getStringValue(sourceAsMap, "url"),
                getStringValue(sourceAsMap, "title"),
                getStringValue(sourceAsMap, "descriptionUser"),
                getStringValue(sourceAsMap, "transcriptionAudio"),
                getStringValue(sourceAsMap, "languageAudio"),
                getStringValue(sourceAsMap, "descriptionVisual"),
                getStringValue(sourceAsMap, "tags"),
                getStringValue(sourceAsMap, "created"),
                getStringValue(sourceAsMap, "popularity", "0"),
                getStringValue(sourceAsMap, "hash"),
                convertToFloatArray(getStringValue(sourceAsMap, "embeddingAudio")),
                convertToFloatArray(getStringValue(sourceAsMap, "embeddingVisual")),
                convertToFloatArray(getStringValue(sourceAsMap, "embeddingUserDescription"))
        );
    }

    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return (value != null) ? value.toString() : "";
    }

    private String getStringValue(Map<String, Object> map, String key, String defaultValue) {
        Object value = map.get(key);
        return (value != null) ? value.toString() : defaultValue;
    }

    private double[] convertToFloatArray(String input) {
        input = input.replaceAll("[\\[\\]]", "");
        String[] parts = input.split(",");
        double[] result = new double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            if (parts[i] == null || parts[i].isEmpty()) {
                result[i] = 1.0f;
            } else {
                result[i] = Double.parseDouble(parts[i].trim());
            }
        }
        return result;
    }
}
