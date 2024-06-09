package com.yappy.search_engine.mapper;

import com.yappy.search_engine.document.Video;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class SearchHitMapper {

    public Video getVideo(org.elasticsearch.search.SearchHit hit) {
        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
        return new Video(
                sourceAsMap.get("uuid").toString(),
                sourceAsMap.getOrDefault("url", "").toString(),
                sourceAsMap.getOrDefault("title", "").toString(),
                sourceAsMap.getOrDefault("description_user", "").toString(),
                sourceAsMap.getOrDefault("description_ml", "").toString(),
                sourceAsMap.getOrDefault("tags", "").toString(),
                sourceAsMap.getOrDefault("created", "").toString(),
                sourceAsMap.getOrDefault("popularity", "0").toString(),
                convertToFloatArray(sourceAsMap.getOrDefault("embedding_audio", "").toString()),
                convertToFloatArray(sourceAsMap.getOrDefault("embedding_visual", "").toString()),
                convertToFloatArray(sourceAsMap.getOrDefault("embedding_user_description", "").toString()),
                convertToFloatArray(sourceAsMap.getOrDefault("embedding_ml_description", "").toString())
        );
    }

    private Double[] convertToDoubleArray(Object obj) {
        if (obj instanceof List<?>) {
            List<?> list = (List<?>) obj;
            return list.stream()
                    .filter(e -> e instanceof Number)
                    .map(e -> ((Number) e).doubleValue())
                    .toArray(Double[]::new);
        }
        return new Double[0];
    }

    private double[] convertToFloatArray(String input) {
        input = input.replaceAll("[\\[\\]]", "");
        String[] parts = input.split(",");
        double[] result = new double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            if (parts[i] == null || parts[i].isEmpty()) {
                result[i] = 0.0f;
            } else {
                result[i] = Double.parseDouble(parts[i].trim());
            }
        }
        return result;
    }
}
