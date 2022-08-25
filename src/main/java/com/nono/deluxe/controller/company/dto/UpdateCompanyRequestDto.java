package com.nono.deluxe.controller.company.dto;

import com.nono.deluxe.domain.company.Company;
import com.nono.deluxe.domain.company.CompanyType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
public class UpdateCompanyRequestDto {

    @NotBlank(message = "Company: name can not Blank")
    @Size(max = 30, message = "companyName max size = 30")
    private String name;

    /**
     * enum 값은 blank 쓸 때 에러가 났다. 왜?
     */
    @NotNull(message = "Company: type can not Null")
    private CompanyType type;

    @NotNull(message = "Company: category can not Null")
    private String category;

    @NotNull(message = "Company: active can not Null")
    private boolean active;
}
