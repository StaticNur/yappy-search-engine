package com.yappy.search_engine.out.model;

import java.util.Arrays;

public class EmbeddingFromText {
    private double[] result;
    private boolean isSuccess;

    public EmbeddingFromText() {
    }

    public EmbeddingFromText(double[] result, boolean isSuccess) {
        this.result = result;
        this.isSuccess = isSuccess;
    }

    public double[] getResult() {
        return result;
    }

    public void setResult(double[] result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    @Override
    public String toString() {
        return "EmbeddingFromText{" +
               "embedding=" + Arrays.toString(result) +
               ", isSuccess=" + isSuccess +
               '}';
    }
}
