package com.nono.deluxe.controller.dto.company;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

import com.nono.deluxe.controller.dto.Meta;
import com.nono.deluxe.domain.company.Company;

import lombok.Data;

@Data
public class ReadCompanyListResponseDTO {
	private Meta meta;
	private List<CompanyResponseDTO> companyList = new ArrayList<>();

	public ReadCompanyListResponseDTO(Page<Company> companyPage) {
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
