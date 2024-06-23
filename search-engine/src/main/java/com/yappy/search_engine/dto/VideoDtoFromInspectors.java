package com.yappy.search_engine.dto;

public class VideoDtoFromInspectors {
    private String link;
    private String description;

    public VideoDtoFromInspectors() {
    }

    public VideoDtoFromInspectors(String link, String description) {
        this.link = link;
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "VideoDtoForInspectors{" +
               "link='" + link + '\'' +
               ", description='" + description + '\'' +
               '}';
    }
}
