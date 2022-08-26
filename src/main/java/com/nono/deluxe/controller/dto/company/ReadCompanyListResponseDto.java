package com.nono.deluxe.controller.dto.company;

import com.nono.deluxe.controller.dto.Meta;
import com.nono.deluxe.domain.company.Company;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Data
public class ReadCompanyListResponseDto {
    private Meta meta;
    private List<ReadCompanyResponseDto> companyList = new ArrayList<>();

    public ReadCompanyListResponseDto(Page<Company> companyPage) {
        List<Company> companyList = companyPage.getContent();
        companyList.forEach(company -> {
            this.companyList.add(new ReadCompanyResponseDto(company));
        });
        companyPage.getTotalElements();
        companyPage.getTotalPages();
        companyPage.isLast();
        companyPage.getPageable().getPageNumber();
        this.meta = new Meta(
                companyPage.getPageable().getPageNumber(),
                companyPage.getNumberOfElements(),
                companyPage.getTotalPages(),
                companyPage.getTotalElements(),
                companyPage.isLast()
        );
    }
}
