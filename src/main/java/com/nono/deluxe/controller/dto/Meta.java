package com.nono.deluxe.controller.dto;

import lombok.Getter;

@Getter
public class Meta {

    private final long page;
    private final long count;
    private final long totalPages;
    private final long totalCount;
    private final boolean lastPage;

    public Meta(long page, long count, long totalPages, long totalCount, boolean lastPage) {
        this.page = page;
        this.count = count;
        this.totalPages = totalPages;
        this.totalCount = totalCount;
        this.lastPage = lastPage;
    }
}
