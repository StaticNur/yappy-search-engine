package com.yappy.search_engine.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "videos", schema = "video_data")
public class MediaContent {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid")
    private UUID uuid;

    @Column(name = "url")
    private String url;

    @Column(name = "title")
    private String title;

    @Column(name = "description_user")
    private String descriptionUser;

    @Column(name = "transcription_audio")
    private String transcriptionAudio;

    @Column(name = "language_audio")
    private String languageAudio;

    @Column(name = "description_visual")
    private String descriptionVisual;

    @Column(name = "tags")
    private String tags;

    @Column(name = "created", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime created;

    @Column(name = "popularity")
    private Integer popularity;

    @Column(name = "hash")
    private String hash;

    @Column(name = "embedding_audio")
    private String embeddingAudio;

    @Column(name = "embedding_visual")
    private String embeddingVisual;

    @Column(name = "embedding_user_description")
    private String embeddingUserDescription;

    @Column(name = "indexing_time")
    private Long indexingTime;

    @Column(name = "ner")
    private String ner;

    public MediaContent() {
    }

    public MediaContent(UUID uuid, String url, String title, String description, String tags, LocalDateTime created) {
        this.uuid = uuid;
        this.url = url;
        this.title = title;
        this.descriptionUser = description;
        this.tags = tags;
        this.created = created;
    }

    public MediaContent(Long id, UUID uuid, String url, String title, String descriptionUser, String transcriptionAudio,
                        String languageAudio, String descriptionVisual, String tags, LocalDateTime created,
                        Integer popularity, String hash, String embeddingAudio, String embeddingVisual,
                        String embeddingUserDescription, Long indexingTime) {
        this.id = id;
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
        this.indexingTime = indexingTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
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

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public Integer getPopularity() {
        return popularity;
    }

    public void setPopularity(Integer popularity) {
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

    public void setEmbeddingAudio(String embeddingAudio) {
        this.embeddingAudio = embeddingAudio;
    }

    public String getEmbeddingVisual() {
        return embeddingVisual;
    }

    public void setEmbeddingVisual(String embeddingVisual) {
        this.embeddingVisual = embeddingVisual;
    }

    public String getEmbeddingUserDescription() {
        return embeddingUserDescription;
    }

    public void setEmbeddingUserDescription(String embeddingUserDescription) {
        this.embeddingUserDescription = embeddingUserDescription;
    }

    public Long getIndexingTime() {
        return indexingTime;
    }

    public void setIndexingTime(Long indexingTime) {
        this.indexingTime = indexingTime;
    }

    public String getNer() {
        return ner;
    }

    public void setNer(String ner) {
        this.ner = ner;
    }

    @Override
    public String toString() {
        return "MediaContent{" +
               "id=" + id +
               ", uuid=" + uuid +
               ", url='" + url + '\'' +
               ", title='" + title + '\'' +
               ", descriptionUser='" + descriptionUser + '\'' +
               ", transcriptionAudio='" + transcriptionAudio + '\'' +
               ", languageAudio='" + languageAudio + '\'' +
               ", descriptionVisual='" + descriptionVisual + '\'' +
               ", tags='" + tags + '\'' +
               ", created=" + created +
               ", popularity=" + popularity +
               ", hash='" + hash + '\'' +
               ", embeddingAudio='" + embeddingAudio + '\'' +
               ", embeddingVisual='" + embeddingVisual + '\'' +
               ", embeddingUserDescription='" + embeddingUserDescription + '\'' +
               ", indexingTime=" + indexingTime +
               ", ner='" + ner + '\'' +
               '}';
    }
}
