package com.nono.deluxe.controller.dto;

import lombok.Getter;

@Getter
public class Meta {

	private long page;
	private long count;
	private long totalPages;
	private long totalCount;
	private boolean lastPage;

	public Meta(long page, long count, long totalPages, long totalCount, boolean lastPage) {
		this.page = page;
		this.count = count;
		this.totalPages = totalPages;
		this.totalCount = totalCount;
		this.lastPage = lastPage;
	}
}
