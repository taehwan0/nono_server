package com.nono.deluxe.domain.notice;

import com.nono.deluxe.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@NoArgsConstructor
@Entity
public class Notice extends BaseTimeEntity {
    @Id @GeneratedValue
    private long id;

    @Column(nullable = false, length = 20)
    private String writer;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = true, length = 500)
    private String content;

    @Column(nullable = false)
    boolean onFocus;

    @Builder
    public Notice(String writer, String title, String content, boolean onFocus) {
        this.writer = writer;
        this.title = title;
        this.content = content;
        this.onFocus = onFocus;
    }

    public void updateNoticeContents(String title, String content, boolean onFocus) {
        this.title = title;
        this.content = content;
        this.onFocus = onFocus;
    }
}
