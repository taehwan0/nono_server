package com.nono.deluxe.application.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.nono.deluxe.domain.company.Company;
import com.nono.deluxe.domain.company.CompanyRepository;
import com.nono.deluxe.domain.document.Document;
import com.nono.deluxe.domain.document.DocumentRepository;
import com.nono.deluxe.presentation.dto.MessageResponseDTO;
import com.nono.deluxe.presentation.dto.company.CompanyResponseDTO;
import com.nono.deluxe.presentation.dto.company.CreateCompanyRequestDTO;
import com.nono.deluxe.presentation.dto.company.GetCompanyListResponseDTO;
import com.nono.deluxe.presentation.dto.company.UpdateCompanyActiveDTO;
import com.nono.deluxe.presentation.dto.company.UpdateCompanyActiveRequestDTO;
import com.nono.deluxe.presentation.dto.company.UpdateCompanyActiveResponseDTO;
import com.nono.deluxe.presentation.dto.company.UpdateCompanyRequestDTO;
import com.nono.deluxe.presentation.dto.document.ReadDocumentListResponseDTO;
import com.nono.deluxe.utils.LocalDateCreator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final DocumentRepository documentRepository;

    @Transactional
    public CompanyResponseDTO createCompany(CreateCompanyRequestDTO requestDTO) {
        Company company = requestDTO.toEntity();
        companyRepository.save(company);

        return new CompanyResponseDTO(company);
    }

    @Transactional(readOnly = true)
    public CompanyResponseDTO readCompany(long companyId) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new NotFoundException("Company: not found id"));

        return new CompanyResponseDTO(company);
    }

    @Transactional(readOnly = true)
    public GetCompanyListResponseDTO getCompanyList(
        String query,
        String column,
        String order,
        int size,
        int page,
        boolean active) {
        Pageable pageRequest =
            PageRequest.of(page, size, Sort.by(new Sort.Order(Sort.Direction.valueOf(order.toUpperCase()), column)));
        Page<Company> companyPage;

        if (active) {
            companyPage = companyRepository.getActiveCompanyList(query, pageRequest); // true -> active 만 읽기
        } else {
            companyPage = companyRepository.getCompanyList(query, pageRequest); // false -> 전체 읽기
        }

        return new GetCompanyListResponseDTO(companyPage);
    }

    @Transactional(readOnly = true)
    public GetCompanyListResponseDTO getInputCompanyList(
        String query,
        String column,
        String order,
        int size,
        int page,
        boolean active
    ) {
        PageRequest pageRequest =
            PageRequest.of(page, size, Sort.by(new Order(Direction.valueOf(order.toUpperCase()), column)));

        if (active) {
            return new GetCompanyListResponseDTO(companyRepository.getActiveInputCompanyList(query, pageRequest));
        }
        return new GetCompanyListResponseDTO(companyRepository.getInputCompanyList(query, pageRequest));
    }

    @Transactional(readOnly = true)
    public GetCompanyListResponseDTO getOutputCompanyList(
        String query,
        String column,
        String order,
        int size,
        int page,
        boolean active
    ) {
        PageRequest pageRequest =
            PageRequest.of(page, size, Sort.by(new Order(Direction.valueOf(order.toUpperCase()), column)));

        if (active) {
            return new GetCompanyListResponseDTO(companyRepository.getActiveOutputCompanyList(query, pageRequest));
        }
        return new GetCompanyListResponseDTO(companyRepository.getOutputCompanyList(query, pageRequest));
    }

    @Transactional(readOnly = true)
    public ReadDocumentListResponseDTO getCompanyDocument(long companyId, String order, int size, int page, int year,
        int month) {
        Pageable limit = PageRequest.of(page, size,
            Sort.by(new Sort.Order(Sort.Direction.valueOf(order.toUpperCase()), "date"),
                new Sort.Order(Sort.Direction.valueOf(order.toUpperCase()), "createdAt")));

        LocalDate fromDate = LocalDateCreator.getDateOfFirstDay(year, month);
        LocalDate toDate = LocalDateCreator.getDateOfLastDay(year, month);

        Page<Document> documentPage = documentRepository.findByCompanyId(companyId, fromDate, toDate, limit);

        return new ReadDocumentListResponseDTO(documentPage);
    }

    @Transactional
    public CompanyResponseDTO updateCompany(long companyId, UpdateCompanyRequestDTO requestDto) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new RuntimeException("Company: not found id"));
        company.update(requestDto.getName(), requestDto.getCategory(), requestDto.isActive());

        return new CompanyResponseDTO(company);
    }

    @Transactional
    public UpdateCompanyActiveResponseDTO updateCompanyActive(UpdateCompanyActiveRequestDTO requestDto) {
        List<UpdateCompanyActiveDTO> companyList = requestDto.getCompanyList();
        List<Company> updatedCompanyList = new ArrayList<>();

        for (UpdateCompanyActiveDTO dto : companyList) {
            Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new NotFoundException("Not Found Company"));
            company.setActive(dto.isActive());

            updatedCompanyList.add(company);
        }

        return new UpdateCompanyActiveResponseDTO(updatedCompanyList);
    }

    @Transactional
    public MessageResponseDTO deleteCompany(long companyId) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new NotFoundException("Company: not found id"));
        company.delete();

        return new MessageResponseDTO(true, "deleted");
    }
}
