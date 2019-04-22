package com.thienan.lovebox.utils;

import java.util.List;

public class PagedResponse<T> {

    private List<T> content;
    private Pagination pagination;

    public PagedResponse() {
    }

    public PagedResponse(List<T> content, Pagination pagination) {
        this.content = content;
        this.pagination = pagination;
    }

    public PagedResponse(List<T> content, int page, int size, long totalElements, int totalPages, boolean first, boolean last) {
        this.content = content;
        this.pagination = new Pagination(page, size, totalElements, totalPages, first, last);
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}
