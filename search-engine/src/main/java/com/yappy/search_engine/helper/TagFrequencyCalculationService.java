package com.yappy.search_engine.helper;

import com.yappy.search_engine.model.VideoFromExcel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TagFrequencyCalculationService {

    public Map<String, Integer> getMapTag(List<VideoFromExcel> videos) {
        Map<String, Integer> tagFrequency = new HashMap<>();
        for (VideoFromExcel video : videos) {
            List<String> tags = extractTags(video.getDescription());
            for (String tag : tags) {
                Integer count = tagFrequency.get(tag);
                if (count == null) {
                    tagFrequency.put(tag, 1);
                } else {
                    count++;
                    tagFrequency.put(tag, count);
                }
            }
        }
        return tagFrequency;
    }

    public static List<String> extractTags(String text) {
        List<String> tags = new ArrayList<>();
        Pattern pattern = Pattern.compile("#[\\wа-яё]+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            tags.add(matcher.group());
        }
        return tags;
    }
}
