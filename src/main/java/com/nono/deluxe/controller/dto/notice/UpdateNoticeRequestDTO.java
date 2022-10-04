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

    @NotBlank
    @Size(max = 100)
    private String title;

    private String content;

    @NotBlank
    private boolean focus;
}
