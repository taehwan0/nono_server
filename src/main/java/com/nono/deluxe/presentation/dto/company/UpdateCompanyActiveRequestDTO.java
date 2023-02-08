package com.nono.deluxe.presentation.dto.company;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateCompanyActiveRequestDTO {

    List<UpdateCompanyActiveDTO> companyList = new ArrayList<>();

    public UpdateCompanyActiveRequestDTO(List<UpdateCompanyActiveDTO> companyList) {
        this.companyList = companyList;
    }
}
