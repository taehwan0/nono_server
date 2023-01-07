package com.nono.deluxe.application.service;

import com.nono.deluxe.domain.company.Company;
import com.nono.deluxe.domain.company.CompanyRepository;
import com.nono.deluxe.domain.document.DocumentRepository;
import com.nono.deluxe.exception.NotFoundException;
import com.nono.deluxe.presentation.dto.MessageResponseDTO;
import com.nono.deluxe.presentation.dto.company.CompanyListResponseDTO;
import com.nono.deluxe.presentation.dto.company.CompanyResponseDTO;
import com.nono.deluxe.presentation.dto.company.CreateCompanyRequestDTO;
import com.nono.deluxe.presentation.dto.company.UpdateCompanyActiveDTO;
import com.nono.deluxe.presentation.dto.company.UpdateCompanyActiveRequestDTO;
import com.nono.deluxe.presentation.dto.company.UpdateCompanyActiveResponseDTO;
import com.nono.deluxe.presentation.dto.company.UpdateCompanyRequestDTO;
import com.nono.deluxe.presentation.dto.document.ReadDocumentListResponseDTO;
import com.nono.deluxe.utils.LocalDateCreator;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final DocumentRepository documentRepository;

    @Transactional
    public CompanyResponseDTO createCompany(CreateCompanyRequestDTO createCompanyRequestDTO) {
        Company company = createCompanyRequestDTO.toEntity();
        companyRepository.save(company);

        return new CompanyResponseDTO(company);
    }

    @Transactional(readOnly = true)
    public CompanyResponseDTO getCompany(long companyId) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new NotFoundException("Company: not found id"));

        return new CompanyResponseDTO(company);
    }

    @Transactional(readOnly = true)
    public CompanyListResponseDTO getCompanyList(PageRequest pageRequest, String query, boolean active) {
        if (active) {
            return new CompanyListResponseDTO(companyRepository.findActivePageByName(query, pageRequest));
        }
        return new CompanyListResponseDTO(companyRepository.findPageByName(query, pageRequest));
    }

    @Transactional(readOnly = true)
    public CompanyListResponseDTO getInputCompanyList(PageRequest pageRequest, String query, boolean active
    ) {
        if (active) {
            return new CompanyListResponseDTO(companyRepository.findActiveInputPageByName(query, pageRequest));
        }
        return new CompanyListResponseDTO(companyRepository.findInputPageByName(query, pageRequest));
    }

    @Transactional(readOnly = true)
    public CompanyListResponseDTO getOutputCompanyList(PageRequest pageRequest, String query, boolean active) {
        if (active) {
            return new CompanyListResponseDTO(companyRepository.findActiveOutputPageName(query, pageRequest));
        }
        return new CompanyListResponseDTO(companyRepository.findOutputPageByName(query, pageRequest));
    }

    @Transactional(readOnly = true)
    public ReadDocumentListResponseDTO getCompanyDocument(
        PageRequest pageRequest,
        long companyId,
        int year,
        int month) {
        LocalDate fromDate = LocalDateCreator.getDateOfFirstDay(year, month);
        LocalDate toDate = LocalDateCreator.getDateOfLastDay(year, month);

        return new ReadDocumentListResponseDTO(
            documentRepository.findPageByCompanyId(companyId, fromDate, toDate, pageRequest));
    }

    @Transactional
    public CompanyResponseDTO updateCompany(long companyId, UpdateCompanyRequestDTO updateCompanyRequestDTO) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new RuntimeException("Company: not found id"));
        company.update(
            updateCompanyRequestDTO.getName(),
            updateCompanyRequestDTO.getCategory(),
            updateCompanyRequestDTO.isActive());

        return new CompanyResponseDTO(company);
    }

    @Transactional
    public UpdateCompanyActiveResponseDTO updateCompanyActive(UpdateCompanyActiveRequestDTO requestDto) {
        List<UpdateCompanyActiveDTO> activeDTOs = requestDto.getCompanyList();

        List<Company> updatedCompanies = activeDTOs.stream()
            .map(dto -> {
                Company company = companyRepository.findById(dto.getCompanyId())
                    .orElseThrow(() -> new NotFoundException("Not Found Company"));
                company.setActive(dto.isActive());
                return company;
            }).collect(Collectors.toList());

        return new UpdateCompanyActiveResponseDTO(updatedCompanies);
    }

    @Transactional
    public MessageResponseDTO deleteCompany(long companyId) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new NotFoundException("Company: not found id"));
        company.delete();

        return MessageResponseDTO.ofSuccess("deleted");
    }
}
