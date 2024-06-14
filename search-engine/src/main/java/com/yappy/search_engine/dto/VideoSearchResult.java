package com.yappy.search_engine.dto;

import com.yappy.search_engine.document.Video;

import java.util.List;

public class VideoSearchResult {
    private long totalHits;
    private List<Video> videos;

    public VideoSearchResult(){}

    public VideoSearchResult(List<Video> videos, long totalHits) {
        this.videos = videos;
        this.totalHits = totalHits;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public long getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(long totalHits) {
        this.totalHits = totalHits;
    }
}

