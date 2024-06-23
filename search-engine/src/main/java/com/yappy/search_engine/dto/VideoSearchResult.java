package com.yappy.search_engine.dto;

import java.util.List;

public class VideoSearchResult {
    private long totalHits;
    private List<VideoResponse> videos;

    public VideoSearchResult() {
    }

    public VideoSearchResult(List<VideoResponse> videos, long totalHits) {
        this.videos = videos;
        this.totalHits = totalHits;
    }

    public List<VideoResponse> getVideos() {
        return videos;
    }

    public void setVideos(List<VideoResponse> videos) {
        this.videos = videos;
    }

    public long getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(long totalHits) {
        this.totalHits = totalHits;
    }
}

