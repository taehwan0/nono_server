package com.nono.deluxe.domain.notice;

import com.nono.deluxe.domain.BaseTimeEntity;
import com.nono.deluxe.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Notice extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "writer_id", nullable = false)
    private User writer;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = true, length = 500)
    private String content;

    @Column(nullable = false)
    boolean onFocused;

    @Builder
    public Notice(User writer, String title, String content, boolean onFocused) {
        this.writer = writer;
        this.title = title;
        this.content = content;
        this.onFocused = onFocused;
    }

    public void updateNoticeContents(String title, String content, boolean onFocus) {
        this.title = title;
        this.content = content;
        this.onFocused = onFocus;
    }
}
