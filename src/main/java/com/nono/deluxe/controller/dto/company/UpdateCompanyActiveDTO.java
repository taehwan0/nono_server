package com.nono.deluxe.controller.dto.company;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class UpdateCompanyActiveDTO {
    @NotBlank
    private long companyId;

    @NotBlank
    private boolean active;
}