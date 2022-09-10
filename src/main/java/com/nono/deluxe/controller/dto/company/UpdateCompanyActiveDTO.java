package com.nono.deluxe.controller.dto.company;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
public class UpdateCompanyActiveDTO {
    @NotNull(message = "companyId can not Null")
    private long companyId;

    @NotNull(message = "companyActive can not Null")
    private boolean active;
}