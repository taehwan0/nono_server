package com.nono.deluxe.service;

import com.nono.deluxe.controller.dto.DeleteApiResponseDto;
import com.nono.deluxe.controller.dto.company.*;
import com.nono.deluxe.domain.company.Company;
import com.nono.deluxe.domain.company.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    @Transactional
    public CreateCompanyResponseDto createCompany(CreateCompanyRequestDto requestDto) {
        Company company = requestDto.toEntity();
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
        Page<Company> companyPage;

        if(active) companyPage = companyRepository.readCompanyList(query, limit);
        else companyPage = companyRepository.readActiveCompanyList(query, limit);

        return new ReadCompanyListResponseDto(companyPage);
    }

    @Transactional
    public UpdateCompanyResponseDto updateCompany(long companyId, UpdateCompanyRequestDto requestDto) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company: not found id"));
        company.update(
                requestDto.getName(),
                requestDto.getType(),
                requestDto.getCategory(),
                requestDto.isActive()
        );

        return new UpdateCompanyResponseDto(company);
    }

    @Transactional
    public UpdateCompanyActiveResponseDto updateCompanyActive(UpdateCompanyActiveRequestDto requestDto) {
        List<UpdateCompanyActiveDto> companyList = requestDto.getCompanyList();
        List<Company> updatedCompanyList = new ArrayList<>();

        for (UpdateCompanyActiveDto dto : companyList) {
            Company company = companyRepository.findById(dto.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Not Found Company"));
            company.setActive(dto.isActive());

            updatedCompanyList.add(company);
        }

        return new UpdateCompanyActiveResponseDto(updatedCompanyList);
    }


    @Transactional
    public DeleteApiResponseDto deleteCompany(long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company: not found id"));
        companyRepository.delete(company);

        return new DeleteApiResponseDto(true, "deleted");
    }
}
