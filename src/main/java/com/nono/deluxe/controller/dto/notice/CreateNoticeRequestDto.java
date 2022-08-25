package com.nono.deluxe.controller.dto.notice;

import com.nono.deluxe.domain.notice.Notice;
import com.nono.deluxe.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateNoticeRequestDto {
    private String title;
    private String content;
    private boolean onFocused;
}
