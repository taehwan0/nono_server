package com.nono.deluxe.notice.presentation.dto.notice;

import com.nono.deluxe.notice.domain.Notice;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class NoticeResponseDTO {

    private Long noticeId;
    private String title;
    private String content;
    private Boolean focus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long writerId;
    private String writer;

    public NoticeResponseDTO() {
    }

    public NoticeResponseDTO(Notice notice) {
        this.noticeId = notice.getId();
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.focus = notice.isFocus();
        this.createdAt = notice.getCreatedAt();
        this.updatedAt = notice.getUpdatedAt();
        this.writerId = notice.getWriter().getId();
        this.writer = notice.getWriter().getName();
    }

    public NoticeResponseDTO(Notice notice, boolean content) {
        this.noticeId = notice.getId();
        this.title = notice.getTitle();
        this.focus = notice.isFocus();
        this.createdAt = notice.getCreatedAt();
        this.updatedAt = notice.getUpdatedAt();
        this.writerId = notice.getWriter().getId();
        this.writer = notice.getWriter().getName();

        if (content) {
            this.content = notice.getContent();
        } else {
            this.content = null;
        }
    }
}
