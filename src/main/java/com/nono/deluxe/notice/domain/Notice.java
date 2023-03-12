package com.nono.deluxe.notice.domain;

import com.nono.deluxe.common.domain.BaseTimeEntity;
import com.nono.deluxe.user.domain.User;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Notice extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false, foreignKey = @ForeignKey(name = "notice_user"))
    private User writer;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = true, columnDefinition = "text")
    private String content;

    @Column(nullable = false)
    private boolean focus;

    @Builder
    public Notice(User writer, String title, String content, boolean focus) {
        this.writer = writer;
        this.title = title;
        this.content = content;
        this.focus = focus;
    }

    public void update(String title, String content, boolean focus) {
        this.title = title;
        this.content = content;
        this.focus = focus;
    }
}
