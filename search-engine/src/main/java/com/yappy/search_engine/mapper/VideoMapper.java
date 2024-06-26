package com.yappy.search_engine.mapper;

import com.yappy.search_engine.document.Video;
import com.yappy.search_engine.dto.VideoDto;
import com.yappy.search_engine.dto.VideoDtoFromInspectors;
import com.yappy.search_engine.dto.VideoResponse;
import com.yappy.search_engine.model.MediaContent;
import com.yappy.search_engine.util.Converter;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class VideoMapper {

    public VideoDto buildVideoFromInspectorsDto(VideoDtoFromInspectors videoDtoFromInspectors) {
        VideoDto videoDto = new VideoDto();
        StringBuilder descriptionUser = new StringBuilder();
        StringBuilder tagsBuilder = new StringBuilder();
        Pattern tagPattern = Pattern.compile("#[\\wА-ЯЁа-яё]+", Pattern.CASE_INSENSITIVE);
        Matcher matcher = tagPattern.matcher(videoDtoFromInspectors.getDescription());
        int lastIndex = 0;
        while (matcher.find()) {
            descriptionUser.append(videoDtoFromInspectors.getDescription(), lastIndex, matcher.start());
            tagsBuilder.append(matcher.group()).append(" ");
            lastIndex = matcher.end();
        }
        descriptionUser.append(videoDtoFromInspectors.getDescription().substring(lastIndex));

        String text = descriptionUser.toString().trim();
        if (text.matches("^(\\s*,\\s*)*$")) {// только запятые
            text = "";
        }
        text = text.replaceAll("[\\s]+", " ");
        String tags = tagsBuilder.toString().trim();

        videoDto.setUrl(videoDtoFromInspectors.getLink());
        videoDto.setDescription(text);
        videoDto.setTags(tags);
        return videoDto;
    }

    public static List<VideoResponse> buildVideoResponse(List<Video> videos) {
        List<VideoResponse> videosResponse = new ArrayList<>();
        for (Video video : videos) {
            videosResponse.add(new VideoResponse(
                    video.getUuid(),
                    video.getUrl(),
                    video.getTitle(),
                    video.getDescriptionUser(),
                    video.getTranscriptionAudio(),
                    video.getLanguageAudio(),
                    video.getDescriptionVisual(),
                    video.getTags(),
                    video.getCreated(),
                    video.getPopularity(),
                    video.getHash(),
                    video.getEmbeddingAudio(),
                    video.getEmbeddingVisual(),
                    video.getEmbeddingUserDescription()
            ));
        }
        return videosResponse;
    }

    public Video buildVideoFromMediaContent(MediaContent mediaContent) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String formattedDate = (mediaContent.getCreated()).format(formatter);
        Integer popularity = mediaContent.getPopularity();
        if (popularity == null) {
            popularity = 0;
        }
        String ner = mediaContent.getNer();
        if (ner == null) {
            ner = "";
        }
        double[] embeddingAudio = Converter.convertToDoubleArray(mediaContent.getEmbeddingAudio());
        if (embeddingAudio.length < 10) {
            embeddingAudio = Converter.EMPTY_VECTOR;
        }
        double[] embeddingVisual = Converter.convertToDoubleArray(mediaContent.getEmbeddingVisual());
        if (embeddingVisual.length < 10) {
            embeddingVisual = Converter.EMPTY_VECTOR;
        }
        double[] embeddingUserDescription = Converter.convertToDoubleArray(mediaContent.getEmbeddingUserDescription());
        if (embeddingUserDescription.length < 10) {
            embeddingUserDescription = Converter.EMPTY_VECTOR;
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
                ner,
                embeddingAudio,
                embeddingVisual,
                embeddingUserDescription
        );
    }
}
