package com.nono.deluxe.controller.dto.company;

import com.nono.deluxe.domain.company.Company;
import com.nono.deluxe.domain.company.CompanyType;
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
