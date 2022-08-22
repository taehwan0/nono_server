package com.nono.deluxe.service;

import com.nono.deluxe.controller.company.dto.*;
import com.nono.deluxe.domain.company.Company;
import com.nono.deluxe.domain.company.CompanyRepository;
import com.nono.deluxe.domain.company.CompanyType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    @Transactional(readOnly = true)
    public ReadCompanyListResponseDto readCompanyList(String query, String column, String order, int size, int page, boolean active) {
        Pageable limit = PageRequest.of(page, size, Sort.by(new Sort.Order(Sort.Direction.valueOf(order.toUpperCase(Locale.ROOT)), column)));
        List<Company> companyList;

        if(active) companyList = companyRepository.readCompanyList(query, limit);
        else companyList = companyRepository.readActiveCompanyList(query, limit);

        return new ReadCompanyListResponseDto(page, companyList);
    }

    @Transactional
    public UpdateCompanyResponseDto updateCompany(long companyId, String name, CompanyType type, String category, boolean active) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company: not found id"));
        company.update(name, type, category, active);

        return new UpdateCompanyResponseDto(company);
    }

    @DeleteMapping
    public DeleteCompanyResponseDto deleteCompany(long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company: not found id"));
        companyRepository.delete(company);

        return new DeleteCompanyResponseDto(true, "deleted");
    }
}
