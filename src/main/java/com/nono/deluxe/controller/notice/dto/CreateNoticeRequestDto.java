package com.nono.deluxe.controller.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateNoticeRequestDto {
    private String writer;
    private String title;
    private String content;
    private boolean onFocus;
}
