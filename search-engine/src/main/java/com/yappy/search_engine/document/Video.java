package com.yappy.search_engine.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "videos")
public class Video {
    @Id
    @Field(type = FieldType.Keyword, name = "uuid")
    private String uuid;

    @Field(type = FieldType.Keyword, name = "url")
    private String url;

    @Field(type = FieldType.Text, name = "title")
    private String title;

    @Field(type = FieldType.Text, name = "description_user")
    private String descriptionUser;

    @Field(type = FieldType.Text, name = "description_ml")
    private String descriptionMl;

    @Field(type = FieldType.Text, name = "tags")
    private String tags;

    @Field(type = FieldType.Date, name = "created")
    private String created;

    @Field(type = FieldType.Text, name = "popularity")
    private String popularity;

    @Field(type = FieldType.Dense_Vector, name = "embedding_audio")
    private double[] embeddingAudio;

    @Field(type = FieldType.Dense_Vector, name = "embedding_visual")
    private double[] embeddingVisual;

    @Field(type = FieldType.Dense_Vector, name = "embedding_user_description")
    private double[] embeddingUserDescription;

    @Field(type = FieldType.Dense_Vector, name = "embedding_ml_description")
    private double[] embeddingMlDescription;

    public Video() {
    }

    public Video(String uuid, String url, String title, String descriptionUser, String tags, String created) {
        this.uuid = uuid;
        this.url = url;
        this.title = title;
        this.descriptionUser = descriptionUser;
        this.tags = tags;
        this.created = created;
    }

    public Video(String uuid, String url, String title, String descriptionUser, String descriptionMl, String tags, String created, String popularity, double[] embeddingAudio, double[] embeddingVisual, double[] embeddingUserDescription, double[] embeddingMlDescription) {
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

    public double[] getEmbeddingAudio() {
        return embeddingAudio;
    }

    public void setEmbeddingAudio(double[] embeddingAudio) {
        this.embeddingAudio = embeddingAudio;
    }

    public double[] getEmbeddingVisual() {
        return embeddingVisual;
    }

    public void setEmbeddingVisual(double[] embeddingVisual) {
        this.embeddingVisual = embeddingVisual;
    }

    public double[] getEmbeddingUserDescription() {
        return embeddingUserDescription;
    }

    public void setEmbeddingUserDescription(double[] embeddingUserDescription) {
        this.embeddingUserDescription = embeddingUserDescription;
    }

    public double[] getEmbeddingMlDescription() {
        return embeddingMlDescription;
    }

    public void setEmbeddingMlDescription(double[] embeddingMlDescription) {
        this.embeddingMlDescription = embeddingMlDescription;
    }
}
