package com.yappy.search_engine.out.model.request;

public class RequestUrl {
    private String videoUrl;

    public RequestUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
