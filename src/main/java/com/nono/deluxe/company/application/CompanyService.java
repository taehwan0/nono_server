package com.nono.deluxe.company.application;

import com.nono.deluxe.common.exception.NotFoundException;
import com.nono.deluxe.common.presentation.dto.MessageResponseDTO;
import com.nono.deluxe.common.utils.LocalDateCreator;
import com.nono.deluxe.company.domain.Company;
import com.nono.deluxe.company.domain.repository.CompanyRepository;
import com.nono.deluxe.company.presentation.dto.company.CompanyListResponseDTO;
import com.nono.deluxe.company.presentation.dto.company.CompanyResponseDTO;
import com.nono.deluxe.company.presentation.dto.company.CreateCompanyRequestDTO;
import com.nono.deluxe.company.presentation.dto.company.UpdateCompanyActiveDTO;
import com.nono.deluxe.company.presentation.dto.company.UpdateCompanyActiveRequestDTO;
import com.nono.deluxe.company.presentation.dto.company.UpdateCompanyActiveResponseDTO;
import com.nono.deluxe.company.presentation.dto.company.UpdateCompanyRequestDTO;
import com.nono.deluxe.document.domain.repository.DocumentRepository;
import com.nono.deluxe.document.presentation.dto.document.ReadDocumentListResponseDTO;
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
        return new CompanyResponseDTO(companyRepository.save(company));
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
        int month,
        boolean withRecord) {
        companyRepository.findById(companyId)
            .orElseThrow(() -> new NotFoundException("Not Found Company"));

        LocalDate fromDate = LocalDateCreator.getDateOfFirstDay(year, month);
        LocalDate toDate = LocalDateCreator.getDateOfLastDay(year, month);

        return new ReadDocumentListResponseDTO(
            documentRepository.findPageByCompanyId(companyId, fromDate, toDate, pageRequest)
            , withRecord);
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
