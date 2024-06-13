package com.yappy.search_engine.out.model.request;

public class TranscribedAudioRequest {
    private String videoUrl;

    public TranscribedAudioRequest(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
