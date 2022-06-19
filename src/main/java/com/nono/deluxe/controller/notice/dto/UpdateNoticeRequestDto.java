package com.nono.deluxe.controller.notice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateNoticeRequestDto {
    private String title;
    private String content;
    private boolean onFocus;
}
