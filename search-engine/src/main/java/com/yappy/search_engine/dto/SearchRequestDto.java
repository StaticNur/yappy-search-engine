package com.yappy.search_engine.dto;

import org.elasticsearch.search.sort.SortOrder;

public class SearchRequestDto extends PagedRequestDto {
    private String typeSearch;
    private String query;
    private String sortBy;
    private SortOrder order;

    public String getTypeSearch() {
        return typeSearch;
    }

    public void setTypeSearch(String typeSearch) {
        this.typeSearch = typeSearch;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public SortOrder getOrder() {
        return order;
    }

    public void setOrder(SortOrder order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "SearchRequestDto{" +
               "typeSearch='" + typeSearch + '\'' +
               ", query='" + query + '\'' +
               ", sortBy='" + sortBy + '\'' +
               ", order=" + order +
               ", page=" + getPage() +
               ", size=" + getSize() +
               '}';
    }
}
