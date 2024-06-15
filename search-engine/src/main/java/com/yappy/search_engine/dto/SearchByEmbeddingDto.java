package com.yappy.search_engine.dto;

public class SearchByEmbeddingDto {
    private String query;
    private int coefficientOfCoincidenceDescriptionUser;
    private int minimumPrefixLengthDescriptionUser;
    private int maximumNumberOfMatchOptionsDescriptionUser;

    private int coefficientOfCoincidenceTag;
    private int minimumPrefixLengthTag;
    private int maximumNumberOfMatchOptionsTag;

    private float boostDescriptionUser;
    private float boostTranscriptionAudio;
    private float boostDescriptionVisual;
    private float boostTags;
    private float boostEmbeddingAudio;
    private float boostEmbeddingVisual;
    private float boostEmbeddingUserDescription;

    public SearchByEmbeddingDto() {}

    public SearchByEmbeddingDto(String query, int coefficientOfCoincidenceDescriptionUser,
                                int minimumPrefixLengthDescriptionUser, int maximumNumberOfMatchOptionsDescriptionUser,
                                int coefficientOfCoincidenceTag, int minimumPrefixLengthTag, int maximumNumberOfMatchOptionsTag, float boostDescriptionUser, float boostTranscriptionAudio, float boostDescriptionVisual, float boostTags, float boostEmbeddingAudio, float boostEmbeddingVisual, float boostEmbeddingUserDescription) {
        this.query = query;
        this.coefficientOfCoincidenceDescriptionUser = coefficientOfCoincidenceDescriptionUser;
        this.minimumPrefixLengthDescriptionUser = minimumPrefixLengthDescriptionUser;
        this.maximumNumberOfMatchOptionsDescriptionUser = maximumNumberOfMatchOptionsDescriptionUser;
        this.coefficientOfCoincidenceTag = coefficientOfCoincidenceTag;
        this.minimumPrefixLengthTag = minimumPrefixLengthTag;
        this.maximumNumberOfMatchOptionsTag = maximumNumberOfMatchOptionsTag;
        this.boostDescriptionUser = boostDescriptionUser;
        this.boostTranscriptionAudio = boostTranscriptionAudio;
        this.boostDescriptionVisual = boostDescriptionVisual;
        this.boostTags = boostTags;
        this.boostEmbeddingAudio = boostEmbeddingAudio;
        this.boostEmbeddingVisual = boostEmbeddingVisual;
        this.boostEmbeddingUserDescription = boostEmbeddingUserDescription;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getCoefficientOfCoincidenceDescriptionUser() {
        return coefficientOfCoincidenceDescriptionUser;
    }

    public void setCoefficientOfCoincidenceDescriptionUser(int coefficientOfCoincidenceDescriptionUser) {
        this.coefficientOfCoincidenceDescriptionUser = coefficientOfCoincidenceDescriptionUser;
    }

    public int getMinimumPrefixLengthDescriptionUser() {
        return minimumPrefixLengthDescriptionUser;
    }

    public void setMinimumPrefixLengthDescriptionUser(int minimumPrefixLengthDescriptionUser) {
        this.minimumPrefixLengthDescriptionUser = minimumPrefixLengthDescriptionUser;
    }

    public int getMaximumNumberOfMatchOptionsDescriptionUser() {
        return maximumNumberOfMatchOptionsDescriptionUser;
    }

    public void setMaximumNumberOfMatchOptionsDescriptionUser(int maximumNumberOfMatchOptionsDescriptionUser) {
        this.maximumNumberOfMatchOptionsDescriptionUser = maximumNumberOfMatchOptionsDescriptionUser;
    }

    public int getCoefficientOfCoincidenceTag() {
        return coefficientOfCoincidenceTag;
    }

    public void setCoefficientOfCoincidenceTag(int coefficientOfCoincidenceTag) {
        this.coefficientOfCoincidenceTag = coefficientOfCoincidenceTag;
    }

    public int getMinimumPrefixLengthTag() {
        return minimumPrefixLengthTag;
    }

    public void setMinimumPrefixLengthTag(int minimumPrefixLengthTag) {
        this.minimumPrefixLengthTag = minimumPrefixLengthTag;
    }

    public int getMaximumNumberOfMatchOptionsTag() {
        return maximumNumberOfMatchOptionsTag;
    }

    public void setMaximumNumberOfMatchOptionsTag(int maximumNumberOfMatchOptionsTag) {
        this.maximumNumberOfMatchOptionsTag = maximumNumberOfMatchOptionsTag;
    }

    public float getBoostDescriptionUser() {
        return boostDescriptionUser;
    }

    public void setBoostDescriptionUser(float boostDescriptionUser) {
        this.boostDescriptionUser = boostDescriptionUser;
    }

    public float getBoostTranscriptionAudio() {
        return boostTranscriptionAudio;
    }

    public void setBoostTranscriptionAudio(float boostTranscriptionAudio) {
        this.boostTranscriptionAudio = boostTranscriptionAudio;
    }

    public float getBoostDescriptionVisual() {
        return boostDescriptionVisual;
    }

    public void setBoostDescriptionVisual(float boostDescriptionVisual) {
        this.boostDescriptionVisual = boostDescriptionVisual;
    }

    public float getBoostTags() {
        return boostTags;
    }

    public void setBoostTags(float boostTags) {
        this.boostTags = boostTags;
    }

    public float getBoostEmbeddingAudio() {
        return boostEmbeddingAudio;
    }

    public void setBoostEmbeddingAudio(float boostEmbeddingAudio) {
        this.boostEmbeddingAudio = boostEmbeddingAudio;
    }

    public float getBoostEmbeddingVisual() {
        return boostEmbeddingVisual;
    }

    public void setBoostEmbeddingVisual(float boostEmbeddingVisual) {
        this.boostEmbeddingVisual = boostEmbeddingVisual;
    }

    public float getBoostEmbeddingUserDescription() {
        return boostEmbeddingUserDescription;
    }

    public void setBoostEmbeddingUserDescription(float boostEmbeddingUserDescription) {
        this.boostEmbeddingUserDescription = boostEmbeddingUserDescription;
    }

    @Override
    public String toString() {
        return "SearchByEmbeddingDto{" +
               "query='" + query + '\'' +
               ", coefficientOfCoincidenceDescriptionUser=" + coefficientOfCoincidenceDescriptionUser +
               ", minimumPrefixLengthDescriptionUser=" + minimumPrefixLengthDescriptionUser +
               ", maximumNumberOfMatchOptionsDescriptionUser=" + maximumNumberOfMatchOptionsDescriptionUser +
               ", coefficientOfCoincidenceTag=" + coefficientOfCoincidenceTag +
               ", minimumPrefixLengthTag=" + minimumPrefixLengthTag +
               ", maximumNumberOfMatchOptionsTag=" + maximumNumberOfMatchOptionsTag +
               ", boostDescriptionUser=" + boostDescriptionUser +
               ", boostTranscriptionAudio=" + boostTranscriptionAudio +
               ", boostDescriptionVisual=" + boostDescriptionVisual +
               ", boostTags=" + boostTags +
               ", boostEmbeddingAudio=" + boostEmbeddingAudio +
               ", boostEmbeddingVisual=" + boostEmbeddingVisual +
               ", boostEmbeddingUserDescription=" + boostEmbeddingUserDescription +
               '}';
    }
}
