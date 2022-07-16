package com.nono.deluxe.controller.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateNoticeRequestDto {
    private String title;
    private String content;
    private boolean onFocused;
}
