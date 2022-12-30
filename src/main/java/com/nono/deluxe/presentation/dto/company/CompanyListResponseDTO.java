package com.nono.deluxe.presentation.dto.company;

import com.nono.deluxe.domain.company.Company;
import com.nono.deluxe.presentation.dto.Meta;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class CompanyListResponseDTO {

    private Meta meta;
    private List<CompanyResponseDTO> companyList = new ArrayList<>();

    public CompanyListResponseDTO(Page<Company> companyPage) {
        List<Company> companyList = companyPage.getContent();
        companyList.forEach(company -> this.companyList.add(new CompanyResponseDTO(company)));
        this.meta = new Meta(
            companyPage.getPageable().getPageNumber() + 1,
            companyPage.getNumberOfElements(),
            companyPage.getTotalPages(),
            companyPage.getTotalElements(),
            companyPage.isLast()
        );
    }
}
