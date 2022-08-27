package com.nono.deluxe.controller.dto.notice;

import com.nono.deluxe.domain.notice.Notice;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateNoticeResponseDto {
    private long noticeId;
    private String title;
    private String content;
    private boolean focus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String writer;

    public UpdateNoticeResponseDto(Notice notice) {
        this.noticeId = notice.getId();
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.focus = notice.isFocus();
        this.createdAt = notice.getCreatedAt();
        this.updatedAt = notice.getUpdatedAt();
        this.writer = notice.getWriter().getName();
    }
}
