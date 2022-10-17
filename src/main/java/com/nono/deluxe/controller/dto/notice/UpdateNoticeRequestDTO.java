package com.nono.deluxe.controller.dto.notice;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateNoticeRequestDTO {

	@NotNull
	@Size(max = 100)
	private String title;

	private String content;

	@NotNull
	private boolean focus;
}
