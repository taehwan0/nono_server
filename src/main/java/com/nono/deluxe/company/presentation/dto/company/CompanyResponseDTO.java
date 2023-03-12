package com.nono.deluxe.company.presentation.dto.company;

import com.nono.deluxe.company.domain.Company;
import com.nono.deluxe.company.domain.CompanyType;
import lombok.Data;

@Data
public class CompanyResponseDTO {

    private long companyId;
    private String name;
    private CompanyType type;
    private String category;
    private boolean active;

    public CompanyResponseDTO(Company company) {
        this.companyId = company.getId();
        this.name = company.getName();
        this.type = company.getType();
        this.category = company.getCategory();
        this.active = company.isActive();
    }
}
