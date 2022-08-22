package com.nono.deluxe.service;

import com.nono.deluxe.controller.company.dto.CreateCompanyResponseDto;
import com.nono.deluxe.controller.company.dto.ReadCompanyResponseDto;
import com.nono.deluxe.controller.company.dto.UpdateCompanyResponseDto;
import com.nono.deluxe.domain.company.Company;
import com.nono.deluxe.domain.company.CompanyRepository;
import com.nono.deluxe.domain.company.CompanyType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    @Transactional
    public CreateCompanyResponseDto createCompany(String name, CompanyType type, String category) {
        Company company = Company.builder()
                .name(name)
                .type(type)
                .category(category)
                .build();
        companyRepository.save(company);

        return new CreateCompanyResponseDto(company);
    }

    @Transactional(readOnly = true)
    public ReadCompanyResponseDto readCompany(long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company: not found id"));

        return new ReadCompanyResponseDto(company);
    }

    @Transactional
    public UpdateCompanyResponseDto updateCompany(long companyId, String name, CompanyType type, String category, boolean active) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company: not found id"));
        company.update(name, type, category, active);

        return new UpdateCompanyResponseDto(company);
    }
}
