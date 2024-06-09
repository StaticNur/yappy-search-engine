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
                convertToDoubleArray(sourceAsMap.get("embedding_audio")),
                convertToDoubleArray(sourceAsMap.get("embedding_visual")),
                convertToDoubleArray(sourceAsMap.get("embedding_user_description")),
                convertToDoubleArray(sourceAsMap.get("embedding_ml_description"))
        );
    }

    private double[] convertToDoubleArray(Object obj) {
        if (obj instanceof List<?>) {
            List<?> list = (List<?>) obj;
            return list.stream()
                    .filter(e -> e instanceof Number)
                    .mapToDouble(e -> ((Number) e).doubleValue())
                    .toArray();
        }
        return new double[768];
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
