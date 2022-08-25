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
public class CreateCompanyRequestDto {

    @NotBlank(message = "Company: companyName can not Blank")
    @Size(max = 30, message = "companyName max size = 30")
    private String name;

    /**
     * enum 값은 blank 쓸 때 에러가 났다. 왜?
     */
    @NotNull(message = "Company: companyType can not Null")
    private CompanyType type;

    private String category;

    /**
     * for test
     * @param name
     * @param type
     * @param category
     */
    public CreateCompanyRequestDto(String name, CompanyType type, String category) {
        this.name = name;
        this.type = type;
        this.category = category;
    }

    public Company toEntity() {
        return Company.builder()
                .name(this.name)
                .type(this.type)
                .category(this.category)
                .build();
    }
}
