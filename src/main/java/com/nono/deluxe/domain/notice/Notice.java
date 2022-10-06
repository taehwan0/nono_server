package com.nono.deluxe.domain.notice;

import com.nono.deluxe.domain.BaseTimeEntity;
import com.nono.deluxe.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@Entity
public class Notice extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @ManyToOne
    @JoinColumn(name = "writer_id", nullable = false, foreignKey = @ForeignKey(name = "notice_user"))
    private User writer;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = true, columnDefinition = "text")
    private String content;

    @NotBlank
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
