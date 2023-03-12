package com.nono.deluxe.company.presentation.dto.company;

import com.nono.deluxe.company.domain.Company;
import com.nono.deluxe.company.domain.CompanyType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateCompanyRequestDTO {

    @NotNull
    @Size(min = 1, max = 30)
    @Pattern(regexp = "^[\\w가-힣0-9]+$")
    private String name;

    /**
     * enum 값은 blank 쓸 때 에러가 났다. 왜?
     */
    @NotNull
    private String type;

    @Size(max = 30)
    private String category;

    public CreateCompanyRequestDTO(String name, String type, String category) {
        this.name = name;
        this.type = type;
        this.category = category;
    }

    public Company toEntity() {
        return Company.builder()
            .name(this.name)
            .type(CompanyType.valueOf(this.type.toUpperCase()))
            .category(this.category)
            .build();
    }
}
