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

    /**
     * enum 값은 blank 쓸 때 에러가 났다. 왜?
     * 불변으로 수정
     */
//    @NotNull(message = "Company: type can not Null")
//    private CompanyType type;

    @NotBlank
    private String category;

    @NotBlank
    private boolean active;
}
