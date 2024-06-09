package com.yappy.search_engine.dto;

public class SearchByEmbeddingDto {
    private float[] embedding;

    public float[] getEmbedding() {
        return embedding;
    }

    public void setEmbedding(float[] embedding) {
        this.embedding = embedding;
    }
}
