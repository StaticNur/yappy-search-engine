package com.yappy.search_engine.dto;

import java.util.Arrays;

public class VideoResponse {
    private String uuid;
    private String url;
    private String title;
    private String descriptionUser;
    private String transcriptionAudio;
    private String languageAudio;
    private String descriptionVisual;
    private String tags;
    private String created;
    private String popularity;
    private String hash;
    private String embeddingAudio;
    private String embeddingVisual;
    private String embeddingUserDescription;

    public VideoResponse() {
    }

    public VideoResponse(String uuid, String url, String title, String descriptionUser, String transcriptionAudio,
                         String languageAudio, String descriptionVisual, String tags, String created, String popularity,
                         String hash, double[] embeddingAudio, double[] embeddingVisual, double[] embeddingUserDescription) {
        this.uuid = uuid;
        this.url = url;
        this.title = title;
        this.descriptionUser = descriptionUser;
        this.transcriptionAudio = transcriptionAudio;
        this.languageAudio = languageAudio;
        this.descriptionVisual = descriptionVisual;
        this.tags = tags;
        this.created = created;
        this.popularity = popularity;
        this.hash = hash;
        this.embeddingAudio = Arrays.toString(embeddingAudio);
        this.embeddingVisual = Arrays.toString(embeddingVisual);
        this.embeddingUserDescription = Arrays.toString(embeddingUserDescription);
    }

    public VideoResponse(String uuid, String url, String title, String descriptionUser, String transcriptionAudio, String languageAudio, String descriptionVisual, String tags, String created, String popularity, String hash, String embeddingAudio, String embeddingVisual, String embeddingUserDescription) {
        this.uuid = uuid;
        this.url = url;
        this.title = title;
        this.descriptionUser = descriptionUser;
        this.transcriptionAudio = transcriptionAudio;
        this.languageAudio = languageAudio;
        this.descriptionVisual = descriptionVisual;
        this.tags = tags;
        this.created = created;
        this.popularity = popularity;
        this.hash = hash;
        this.embeddingAudio = embeddingAudio;
        this.embeddingVisual = embeddingVisual;
        this.embeddingUserDescription = embeddingUserDescription;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescriptionUser() {
        return descriptionUser;
    }

    public void setDescriptionUser(String descriptionUser) {
        this.descriptionUser = descriptionUser;
    }

    public String getTranscriptionAudio() {
        return transcriptionAudio;
    }

    public void setTranscriptionAudio(String transcriptionAudio) {
        this.transcriptionAudio = transcriptionAudio;
    }

    public String getLanguageAudio() {
        return languageAudio;
    }

    public void setLanguageAudio(String languageAudio) {
        this.languageAudio = languageAudio;
    }

    public String getDescriptionVisual() {
        return descriptionVisual;
    }

    public void setDescriptionVisual(String descriptionVisual) {
        this.descriptionVisual = descriptionVisual;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getEmbeddingAudio() {
        return embeddingAudio;
    }

    public void setEmbeddingAudio(double[] embeddingAudio) {
        this.embeddingAudio = Arrays.toString(embeddingAudio);
    }

    public String getEmbeddingVisual() {
        return embeddingVisual;
    }

    public void setEmbeddingVisual(double[] embeddingVisual) {
        this.embeddingVisual = Arrays.toString(embeddingVisual);
    }

    public String getEmbeddingUserDescription() {
        return embeddingUserDescription;
    }

    public void setEmbeddingUserDescription(double[] embeddingUserDescription) {
        this.embeddingUserDescription = Arrays.toString(embeddingUserDescription);
    }

    public void setEmbeddingAudio(String embeddingAudio) {
        this.embeddingAudio = embeddingAudio;
    }

    public void setEmbeddingVisual(String embeddingVisual) {
        this.embeddingVisual = embeddingVisual;
    }

    public void setEmbeddingUserDescription(String embeddingUserDescription) {
        this.embeddingUserDescription = embeddingUserDescription;
    }

    @Override
    public String toString() {
        return "VideoResponse{" +
               "uuid='" + uuid + '\'' +
               ", url='" + url + '\'' +
               ", title='" + title + '\'' +
               ", descriptionUser='" + descriptionUser + '\'' +
               ", transcriptionAudio='" + transcriptionAudio + '\'' +
               ", languageAudio='" + languageAudio + '\'' +
               ", descriptionVisual='" + descriptionVisual + '\'' +
               ", tags='" + tags + '\'' +
               ", created='" + created + '\'' +
               ", popularity='" + popularity + '\'' +
               ", hash='" + hash + '\'' +
               ", embeddingAudio='" + embeddingAudio + '\'' +
               ", embeddingVisual='" + embeddingVisual + '\'' +
               ", embeddingUserDescription='" + embeddingUserDescription + '\'' +
               '}';
    }
}
