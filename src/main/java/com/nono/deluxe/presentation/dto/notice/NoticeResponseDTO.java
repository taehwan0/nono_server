package com.nono.deluxe.presentation.dto.notice;

import com.nono.deluxe.domain.notice.Notice;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class NoticeResponseDTO {

    private long noticeId;
    private String title;
    private String content;
    private boolean focus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String writer;

    public NoticeResponseDTO(Notice notice) {
        this.noticeId = notice.getId();
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.focus = notice.isFocus();
        this.createdAt = notice.getCreatedAt();
        this.updatedAt = notice.getUpdatedAt();
        this.writer = notice.getWriter().getName();
    }

    public NoticeResponseDTO(Notice notice, boolean content) {
        this.noticeId = notice.getId();
        this.title = notice.getTitle();
        if (content) {
            this.content = notice.getContent();
        } else {
            this.content = null;
        }
        this.focus = notice.isFocus();
        this.createdAt = notice.getCreatedAt();
        this.updatedAt = notice.getUpdatedAt();
        this.writer = notice.getWriter().getName();
    }
}
