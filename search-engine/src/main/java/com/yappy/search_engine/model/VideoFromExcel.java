package com.yappy.search_engine.model;

public class VideoFromExcel {
    private String url;
    private String description;

    public VideoFromExcel(String url, String description) {
        this.url = url;
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "VideoFromExcel{" +
               "url='" + url + '\'' +
               ", description='" + description + '\'' +
               '}';
    }
}
