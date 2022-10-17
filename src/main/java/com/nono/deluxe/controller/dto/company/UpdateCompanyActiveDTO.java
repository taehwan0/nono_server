package com.nono.deluxe.controller.dto.company;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateCompanyActiveDTO {
	@NotNull
	private long companyId;

	@NotNull
	private boolean active;
}
