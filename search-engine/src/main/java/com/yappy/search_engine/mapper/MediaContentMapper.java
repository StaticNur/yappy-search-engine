package com.yappy.search_engine.mapper;

import com.yappy.search_engine.dto.VideoDto;
import com.yappy.search_engine.model.MediaContent;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class MediaContentMapper {

    public MediaContent buildVideoFromDto(VideoDto videoDto) {
        return new MediaContent(UUID.randomUUID(),
                videoDto.getUrl(),
                videoDto.getTitle(),
                videoDto.getDescription(),
                videoDto.getTags(),
                LocalDateTime.now());
    }
}
