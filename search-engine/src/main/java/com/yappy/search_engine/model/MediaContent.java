package com.yappy.search_engine.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

@Entity
@Table(name = "videos", schema = "VIDEO_DATA")
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

    @Column(name = "description_ml")
    private String descriptionMl;

    @Column(name = "tags")
    private String tags;

    @Column(name = "created")
    private LocalDateTime created;

    @Column(name = "popularity")
    private Integer popularity;

    @Column(name = "embedding_audio")
    private String embeddingAudio;

    @Column(name = "embedding_visual")
    private String embeddingVisual;

    @Column(name = "embedding_user_description")
    private String embeddingUserDescription;

    @Column(name = "embedding_ml_description")
    private String embeddingMlDescription;

    public MediaContent() {
    }
    public MediaContent(UUID uuid, String url, String title, String descriptionUser, String tags, LocalDateTime created) {
        this.uuid = uuid;
        this.url = url;
        this.title = title;
        this.descriptionUser = descriptionUser;
        this.tags = tags;
        this.created = created;
    }

    public MediaContent(Long id, UUID uuid, String url, String title, String descriptionUser, String descriptionMl,
                        String tags, LocalDateTime created, Integer popularity, String embeddingAudio,
                        String embeddingVisual, String embeddingUserDescription, String embeddingMlDescription) {
        this.id = id;
        this.uuid = uuid;
        this.url = url;
        this.title = title;
        this.descriptionUser = descriptionUser;
        this.descriptionMl = descriptionMl;
        this.tags = tags;
        this.created = created;
        this.popularity = popularity;
        this.embeddingAudio = embeddingAudio;
        this.embeddingVisual = embeddingVisual;
        this.embeddingUserDescription = embeddingUserDescription;
        this.embeddingMlDescription = embeddingMlDescription;
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

    public String getDescriptionMl() {
        return descriptionMl;
    }

    public void setDescriptionMl(String descriptionMl) {
        this.descriptionMl = descriptionMl;
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

    public String getEmbeddingMlDescription() {
        return embeddingMlDescription;
    }

    public void setEmbeddingMlDescription(String embeddingMlDescription) {
        this.embeddingMlDescription = embeddingMlDescription;
    }

    @Override
    public String toString() {
        return "MediaContent{" +
               "id=" + id +
               ", uuid=" + uuid +
               ", url='" + url + '\'' +
               ", title='" + title + '\'' +
               ", descriptionUser='" + descriptionUser + '\'' +
               ", descriptionMl='" + descriptionMl + '\'' +
               ", tags='" + tags + '\'' +
               ", created=" + created +
               ", popularity=" + popularity +
               ", embeddingAudio=" + embeddingAudio +
               ", embeddingVisual=" + embeddingVisual +
               ", embeddingUserDescription=" + embeddingUserDescription +
               ", embeddingMlDescription=" + embeddingMlDescription +
               '}';
    }
}
