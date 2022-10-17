package com.nono.deluxe.controller.dto.notice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

import com.nono.deluxe.controller.dto.Meta;
import com.nono.deluxe.domain.notice.Notice;

import lombok.Data;

@Data
public class ReadNoticeListResponseDTO {
	private Meta meta;
	private List<NoticeResponseDTO> noticeList = new ArrayList<>();

	public ReadNoticeListResponseDTO(Page<Notice> noticePage, boolean content) {
		List<Notice> noticeList = noticePage.getContent();
		noticeList.forEach(notice -> this.noticeList.add(new NoticeResponseDTO(notice, content)));
		noticePage.getTotalElements();
		noticePage.getTotalPages();
		noticePage.isLast();
		noticePage.getPageable().getPageNumber();
		this.meta = new Meta(
				noticePage.getPageable().getPageNumber() + 1,
				noticePage.getNumberOfElements(),
				noticePage.getTotalPages(),
				noticePage.getTotalElements(),
				noticePage.isLast()
		);
	}
}
