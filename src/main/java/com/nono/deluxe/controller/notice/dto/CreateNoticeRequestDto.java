package com.nono.deluxe.controller.notice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@NoArgsConstructor
public class CreateNoticeRequestDto {
    private String writer;
    private String title;
    private String content;
    private boolean onFocus;
}
