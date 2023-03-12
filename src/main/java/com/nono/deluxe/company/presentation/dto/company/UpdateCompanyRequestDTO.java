package com.nono.deluxe.company.presentation.dto.company;

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

    @NotNull
    private boolean active;

    public UpdateCompanyRequestDTO(String name, String category, boolean active) {
        this.name = name;
        this.category = category;
        this.active = active;
    }
}
