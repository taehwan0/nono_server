package com.nono.deluxe.controller.dto.notice;

import com.nono.deluxe.domain.notice.Notice;
import com.nono.deluxe.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateNoticeRequestDTO {

    @NotBlank(message = "noticeTitle can not Blank")
    @Size(max = 100, message = "noticeTitle max size = 100")
    private String title;

    private String content;

    @NotBlank(message = "noticeFocus can not Blank")
    private boolean focus;

    public Notice toEntity(User writer) {
        return Notice.builder()
                .title(this.title)
                .content(this.content)
                .focus(this.focus)
                .writer(writer)
                .build();
    }
}
