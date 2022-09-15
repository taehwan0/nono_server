package com.nono.deluxe.service;

import com.nono.deluxe.controller.dto.MessageResponseDTO;
import com.nono.deluxe.controller.dto.company.*;
import com.nono.deluxe.controller.dto.document.ReadDocumentListResponseDTO;
import com.nono.deluxe.domain.company.Company;
import com.nono.deluxe.domain.company.CompanyRepository;
import com.nono.deluxe.domain.document.Document;
import com.nono.deluxe.domain.document.DocumentRepository;
import com.nono.deluxe.domain.record.Record;
import com.nono.deluxe.domain.record.RecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final RecordRepository recordRepository;
    private final DocumentRepository documentRepository;

    @Transactional
    public CompanyResponseDTO createCompany(CreateCompanyRequestDTO requestDto) {
        Company company = requestDto.toEntity();
        companyRepository.save(company);

        return new CompanyResponseDTO(company);
    }

    @Transactional(readOnly = true)
    public CompanyResponseDTO readCompany(long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company: not found id"));

        return new CompanyResponseDTO(company);
    }

    @Transactional(readOnly = true)
    public ReadCompanyListResponseDTO readCompanyList(String query, String column, String order, int size, int page, boolean active) {
        Pageable limit = PageRequest.of(page, size, Sort.by(new Sort.Order(Sort.Direction.valueOf(order.toUpperCase(Locale.ROOT)), column)));
        Page<Company> companyPage;

        if(active) companyPage = companyRepository.readCompanyList(query, limit);
        else companyPage = companyRepository.readActiveCompanyList(query, limit);

        return new ReadCompanyListResponseDTO(companyPage);
    }

    @Transactional(readOnly = true)
    public ReadDocumentListResponseDTO readCompanyDocument(long companyId, String order, int size, int page, int year, int month) {
        Pageable limit = PageRequest.of(page, size, Sort.by(
                new Sort.Order(Sort.Direction.valueOf(order.toUpperCase()), "date"),
                new Sort.Order(Sort.Direction.valueOf(order.toUpperCase()), "createdAt")));

        if(year == 0) year = LocalDate.now().getYear();
        int toMonth = month;
        if(month == 0) {
            month = 1;
            toMonth = 12;
        }

        LocalDate fromDate = LocalDate.of(year, month, 1);
        LocalDate toDate = LocalDate.of(year, toMonth, LocalDate.of(year, toMonth, 1).lengthOfMonth());
        // 테스트 해보기

        Page<Document> documentPage = documentRepository.findByCompanyId(companyId, fromDate, toDate, limit);

        return new ReadDocumentListResponseDTO(documentPage);
    }

    @Transactional(readOnly = true)
    public CompanyRecordResponseDTO readCompanyRecord(long companyId, int year, int month) {

        if(year == 0) year = LocalDate.now().getYear();
        int toMonth = month;
        if(month == 0) {
            month = 1;
            toMonth = 12;
        }

        LocalDate fromDate = LocalDate.of(year, month, 1);
        LocalDate toDate = LocalDate.of(year, toMonth, LocalDate.of(year, toMonth, 1).lengthOfMonth());

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Not Found Company"));
        List<Record> recordList = recordRepository.findByCompanyId(companyId, fromDate, toDate);

        return new CompanyRecordResponseDTO(company, recordList);
    }



    @Transactional
    public CompanyResponseDTO updateCompany(long companyId, UpdateCompanyRequestDTO requestDto) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company: not found id"));
        company.update(
                requestDto.getName(),
                requestDto.getCategory(),
                requestDto.isActive()
        );

        return new CompanyResponseDTO(company);
    }

    @Transactional
    public UpdateCompanyActiveResponseDTO updateCompanyActive(UpdateCompanyActiveRequestDTO requestDto) {
        List<UpdateCompanyActiveDTO> companyList = requestDto.getCompanyList();
        List<Company> updatedCompanyList = new ArrayList<>();

        for (UpdateCompanyActiveDTO dto : companyList) {
            Company company = companyRepository.findById(dto.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Not Found Company"));
            company.setActive(dto.isActive());

            updatedCompanyList.add(company);
        }

        return new UpdateCompanyActiveResponseDTO(updatedCompanyList);
    }


    @Transactional
    public MessageResponseDTO deleteCompany(long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company: not found id"));
        company.delete();

        return new MessageResponseDTO(true, "deleted");
    }
}
