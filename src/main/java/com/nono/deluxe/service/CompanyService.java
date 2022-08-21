package com.nono.deluxe.service;

import com.nono.deluxe.controller.company.dto.CreateCompanyResponseDto;
import com.nono.deluxe.domain.company.Company;
import com.nono.deluxe.domain.company.CompanyRepository;
import com.nono.deluxe.domain.company.CompanyType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CreateCompanyResponseDto createCompany(String name, CompanyType companyType, String category) {
        Company company = Company.builder()
                .name(name)
                .type(companyType)
                .category(category)
                .build();

        return new CreateCompanyResponseDto(companyRepository.save(company));
    }
}
