package com.yappy.search_engine.model;

public class Embedding {
    private String url;
    private String transcription;
    private String embedding;

    public Embedding() {
    }

    public Embedding(String url, String transcription, String embedding) {
        this.url = url;
        this.transcription = transcription;
        this.embedding = embedding;
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

    public String getEmbedding() {
        return embedding;
    }

    public void setEmbedding(String embedding) {
        this.embedding = embedding;
    }

    @Override
    public String toString() {
        return "Embedding{" +
               "url='" + url + '\'' +
               ", transcription='" + transcription + '\'' +
               ", embedding='" + embedding + '\'' +
               '}';
    }
}
