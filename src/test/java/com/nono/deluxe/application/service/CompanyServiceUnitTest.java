package com.nono.deluxe.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.nono.deluxe.domain.company.Company;
import com.nono.deluxe.domain.company.CompanyRepository;
import com.nono.deluxe.domain.company.CompanyType;
import com.nono.deluxe.presentation.dto.company.CompanyResponseDTO;
import com.nono.deluxe.presentation.dto.company.CreateCompanyRequestDTO;
import com.nono.deluxe.presentation.dto.company.UpdateCompanyRequestDTO;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("거래처 서비스 단위 테스트")
@ExtendWith(MockitoExtension.class)
public class CompanyServiceUnitTest {

    @InjectMocks
    private CompanyService companyService;

    @Mock
    private CompanyRepository companyRepository;

    @Test
    @DisplayName("거래처가 생성된다.")
    void createCompany() {
        CreateCompanyRequestDTO createCompanyRequestDTO =
            new CreateCompanyRequestDTO("company", "input", "category");

        Company company = Company.builder()
            .name("company")
            .category("category")
            .type(CompanyType.INPUT)
            .build();

        ReflectionTestUtils.setField(company, "id", 1L);

        // mock
        given(companyRepository.save(any())).willReturn(company);

        // when
        CompanyResponseDTO companyResponseDTO = companyService.createCompany(createCompanyRequestDTO);

        assertThat(companyResponseDTO.getCompanyId()).isEqualTo(1L);
        assertThat(companyResponseDTO.getName()).isEqualTo(createCompanyRequestDTO.getName());
        assertThat(companyResponseDTO.getType())
            .isEqualTo(CompanyType.valueOf(createCompanyRequestDTO.getType().toUpperCase()));
        assertThat(companyResponseDTO.getCategory()).isEqualTo(createCompanyRequestDTO.getCategory());
        assertThat(companyResponseDTO.isActive()).isTrue();
    }

    @Test
    @DisplayName("거래처를 조회한다.")
    void getCompany() {
        Company company = Company.builder()
            .name("company")
            .category("category")
            .type(CompanyType.INPUT)
            .build();

        ReflectionTestUtils.setField(company, "id", 1L);

        // mock
        given(companyRepository.findById(1L)).willReturn(Optional.of(company));

        // when
        CompanyResponseDTO companyResponseDTO = companyService.getCompany(1L);

        assertThat(companyResponseDTO.getCompanyId()).isEqualTo(1L);
        assertThat(companyResponseDTO.getName()).isEqualTo("company");
        assertThat(companyResponseDTO.getType()).isEqualTo(CompanyType.INPUT);
        assertThat(companyResponseDTO.getCategory()).isEqualTo("category");
        assertThat(companyResponseDTO.isActive()).isTrue();
    }

    @Test
    @DisplayName("거래처를 수정한다.")
    void updateCompany() {
        Company company = Company.builder()
            .name("company")
            .category("category")
            .type(CompanyType.INPUT)
            .build();
        ReflectionTestUtils.setField(company, "id", 1L);

        UpdateCompanyRequestDTO updateCompanyRequestDTO =
            new UpdateCompanyRequestDTO("company2", "category2", false);

        // mock
        given(companyRepository.findById(1L)).willReturn(Optional.of(company));

        // when
        CompanyResponseDTO companyResponseDTO = companyService.updateCompany(1L, updateCompanyRequestDTO);

        assertThat(companyResponseDTO.getCompanyId()).isEqualTo(1L);
        assertThat(companyResponseDTO.getName()).isEqualTo("company2");
        assertThat(companyResponseDTO.getType()).isEqualTo(CompanyType.INPUT);
        assertThat(companyResponseDTO.getCategory()).isEqualTo("category2");
        assertThat(companyResponseDTO.isActive()).isFalse();
    }

    @Test
    @DisplayName("거래처를 삭제한다.")
    void deleteCompany() {
        Company company = Company.builder()
            .name("company")
            .category("category")
            .type(CompanyType.INPUT)
            .build();
        ReflectionTestUtils.setField(company, "id", 1L);

        // mock
        given(companyRepository.findById(1L)).willReturn(Optional.of(company));

        // when
        companyService.deleteCompany(1L);

        assertThat(company.getId()).isEqualTo(1L);
        assertThat(company.getName()).isNull();
        assertThat(company.isActive()).isFalse();
        assertThat(company.isDeleted()).isTrue();
    }
}
