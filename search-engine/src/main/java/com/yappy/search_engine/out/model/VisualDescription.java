package com.yappy.search_engine.out.model;

public class VisualDescription {
    private String result;
    private boolean isSuccess;

    public VisualDescription() {
    }

    public VisualDescription(String result, boolean isSuccess) {
        this.result = result;
        this.isSuccess = isSuccess;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
