package com.yappy.search_engine.dto;

public class PagedRequestDto {
    private static final int DEFAULT_SIZE = 15;

    private int page;
    private int size;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size != 0 ? size : DEFAULT_SIZE;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
