package com.nono.deluxe.controller.dto.company;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateCompanyRequestDTO {

	@NotNull
	@Size(min = 1, max = 30)
	private String name;

	@Size(max = 30)
	private String category;

	@NotBlank
	private boolean active;
}
