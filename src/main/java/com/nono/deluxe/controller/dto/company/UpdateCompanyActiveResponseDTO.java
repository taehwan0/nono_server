package com.nono.deluxe.controller.dto.company;

import com.nono.deluxe.domain.company.Company;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UpdateCompanyActiveResponseDTO {
    private List<CompanyResponseDTO> companyList = new ArrayList<>();

    public UpdateCompanyActiveResponseDTO(List<Company> updateCompanyList) {
        for (Company company : updateCompanyList) {
            this.companyList.add(new CompanyResponseDTO(company));
        }
    }
}