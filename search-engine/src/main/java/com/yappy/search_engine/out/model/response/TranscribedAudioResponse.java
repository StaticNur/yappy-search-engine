package com.yappy.search_engine.out.model.response;

public class TranscribedAudioResponse {
    private String text;
    private String languages;

    public TranscribedAudioResponse() {
    }

    public TranscribedAudioResponse(String text, String languages) {
        this.text = text;
        this.languages = languages;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }
}
