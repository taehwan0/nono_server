package com.nono.deluxe.controller.dto.company;

import com.nono.deluxe.domain.company.Company;
import lombok.Data;

@Data
public class CreateCompanyResponseDto {
    private long companyId;
    private String name;
    private String type;
    private String category;
    private boolean active;

    public CreateCompanyResponseDto(Company company) {
        this.companyId = company.getId();
        this.name = company.getName();
        this.type = company.getType().toString();
        this.category = company.getCategory();
        this.active = company.isActivate();
    }
}
