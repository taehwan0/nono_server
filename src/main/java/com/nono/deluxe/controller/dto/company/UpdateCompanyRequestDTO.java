package com.nono.deluxe.controller.dto.company;

import com.nono.deluxe.domain.company.CompanyType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
public class UpdateCompanyRequestDTO {

    @NotBlank
    @Size(max = 30)
    private String name;

    @Size(max = 30)
    private String category;

    @NotBlank
    private boolean active;
}
