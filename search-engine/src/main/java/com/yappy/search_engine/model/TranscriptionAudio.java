package com.yappy.search_engine.model;

public class TranscriptionAudio {
    private String url;
    private String transcription;
    private String language;
    public TranscriptionAudio() {
    }
    public TranscriptionAudio(String url, String transcription, String language) {
        this.url = url;
        this.transcription = transcription;
        this.language = language;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "TranscriptionAudio{" +
               "url='" + url + '\'' +
               ", transcription='" + transcription + '\'' +
               ", language='" + language + '\'' +
               '}';
    }
}
