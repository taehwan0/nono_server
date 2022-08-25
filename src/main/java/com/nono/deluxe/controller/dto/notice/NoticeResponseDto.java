package com.nono.deluxe.controller.dto.notice;

import com.nono.deluxe.domain.notice.Notice;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoticeResponseDto {
    private long noticeId;
    private String title;
    private String content;
    private boolean onFocused;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private String writer;

    public NoticeResponseDto(Notice notice) {
        this.noticeId = notice.getId();
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.onFocused = notice.isFocus();
        this.createAt = notice.getCreatedAt();
        this.updateAt = notice.getUpdatedAt();
        this.writer = notice.getWriter().getName();
    }

}
