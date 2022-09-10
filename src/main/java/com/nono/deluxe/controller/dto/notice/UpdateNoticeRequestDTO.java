package com.nono.deluxe.controller.dto.notice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateNoticeRequestDTO {

    @NotBlank(message = "noticeTitle can not Blank")
    @Size(max = 100, message = "noticeTitle max size = 100")
    private String title;

    private String content;

    @NotBlank(message = "noticeFocus can not Blank")
    private boolean focus;
}
