package com.nono.deluxe.notice.presentation.dto.notice;

import com.nono.deluxe.notice.domain.Notice;
import com.nono.deluxe.user.domain.User;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateNoticeRequestDTO {

    @NotNull
    @Size(max = 100)
    private String title;

    private String content;

    @NotNull
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
