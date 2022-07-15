package com.nono.deluxe.controller.notice.dto;

import com.nono.deluxe.domain.notice.Notice;
import com.nono.deluxe.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateNoticeRequestDto {
    private User writer;
    private String title;
    private String content;
    private boolean onFocused;

    public Notice toEntity() {
        return Notice.builder()
                .title(title)
                .content(content)
                .writer(writer)
                .onFocused(onFocused)
                .build();
    }
}
