package com.nono.deluxe.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.nono.deluxe.common.exception.NotFoundException;
import com.nono.deluxe.company.application.CompanyService;
import com.nono.deluxe.company.domain.Company;
import com.nono.deluxe.company.domain.CompanyType;
import com.nono.deluxe.company.domain.repository.CompanyRepository;
import com.nono.deluxe.company.presentation.dto.company.CompanyListResponseDTO;
import com.nono.deluxe.company.presentation.dto.company.CompanyResponseDTO;
import com.nono.deluxe.company.presentation.dto.company.CreateCompanyRequestDTO;
import com.nono.deluxe.company.presentation.dto.company.UpdateCompanyActiveDTO;
import com.nono.deluxe.company.presentation.dto.company.UpdateCompanyActiveRequestDTO;
import com.nono.deluxe.company.presentation.dto.company.UpdateCompanyRequestDTO;
import com.nono.deluxe.document.presentation.dto.document.ReadDocumentListResponseDTO;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.transaction.annotation.Transactional;

@DisplayName("거래처 서비스 스프링 테스트")
@Transactional
@SpringBootTest
class CompanyServiceTest {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CompanyRepository companyRepository;

    @Nested
    @DisplayName("생성 테스트")
    class create {

        @Test
        @DisplayName("정상적으로 생성된다.")
        void createCompany() {
            // given
            CreateCompanyRequestDTO createCompanyRequestDTO =
                new CreateCompanyRequestDTO("company", "input", "category");

            // when
            CompanyResponseDTO company = companyService.createCompany(createCompanyRequestDTO);

            // then
            assertThat(company.getName()).isEqualTo("company");
            assertThat(company.getType()).isEqualTo(CompanyType.INPUT);
            assertThat(company.getCategory()).isEqualTo("category");
            assertThat(company.isActive()).isTrue();
        }

        @Test
        @DisplayName("중복된 이름으로 생성을 시도한다면 실패한다.")
        void createCompanyWithDuplicateName() {
            // given
            CreateCompanyRequestDTO createCompanyRequestDTO =
                new CreateCompanyRequestDTO("company", "input", "category");

            companyService.createCompany(createCompanyRequestDTO);

            // when, then
            assertThatThrownBy(() -> companyService.createCompany(createCompanyRequestDTO))
                .isInstanceOf(DataIntegrityViolationException.class);
        }
    }

    @Nested
    @DisplayName("조회 테스트")
    class read {

        @Test
        @DisplayName("정상적으로 조회된다.")
        void getCompany() {
            CompanyResponseDTO companyResponseDTO =
                createEntity("name", CompanyType.INPUT, "category");

            CompanyResponseDTO getCompanyResponseDTO =
                companyService.getCompany(companyResponseDTO.getCompanyId());

            assertThat(companyResponseDTO).isEqualTo(getCompanyResponseDTO);
        }

        @Test
        @DisplayName("존재하지 않는 거래처의 조회를 요청하면 실패한다.")
        void getCompanyByNotFoundId() {
            assertThatThrownBy(() -> companyService.getCompany(0L))
                .isInstanceOf(NotFoundException.class);
        }

        @Test
        @DisplayName("거래처의 목록을 정상적으로 조회한다.")
        void getCompanies() {
            List<CompanyResponseDTO> companies = List.of(
                createEntity("company1", CompanyType.INPUT, ""),
                createEntity("company2", CompanyType.INPUT, "")
            );

            PageRequest pageRequest
                = PageRequest.of(0, 10, Sort.by(new Order(Direction.ASC, "name")));

            CompanyListResponseDTO companyList = companyService.getCompanyList(pageRequest, "", false);

            assertThat(companyList.getCompanyList())
                .containsAll(companies);
        }

        @Test
        @DisplayName("정의되지 않은 정렬 기준으로 목록을 요청하면 실패한다.")
        void getCompaniesByUnknownColumn() {
            PageRequest pageRequest
                = PageRequest.of(0, 10, Sort.by(new Order(Direction.ASC, "companyName")));

            assertThatThrownBy(() -> companyService.getCompanyList(pageRequest, "", false))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
        }

        @Test
        @DisplayName("입고 거래처만 조회한다.")
        void getCompaniesOfInputType() {
            CompanyResponseDTO company1 = createEntity("company1", CompanyType.INPUT, "");
            CompanyResponseDTO company2 = createEntity("company2", CompanyType.OUTPUT, "");

            PageRequest pageRequest
                = PageRequest.of(0, 10, Sort.by(new Order(Direction.ASC, "name")));

            List<CompanyResponseDTO> companyList =
                companyService.getInputCompanyList(pageRequest, "", false).getCompanyList();

            assertThat(companyList).contains(company1);
            assertThat(companyList).doesNotContain(company2);
        }

        @Test
        @DisplayName("출고 거래처만 조회한다.")
        void getCompaniesOfOutputType() {
            CompanyResponseDTO company1 = createEntity("company1", CompanyType.INPUT, "");
            CompanyResponseDTO company2 = createEntity("company2", CompanyType.OUTPUT, "");

            PageRequest pageRequest
                = PageRequest.of(0, 10, Sort.by(new Order(Direction.ASC, "name")));

            List<CompanyResponseDTO> companyList =
                companyService.getOutputCompanyList(pageRequest, "", false).getCompanyList();

            assertThat(companyList).doesNotContain(company1);
            assertThat(companyList).contains(company2);
        }

        @Test
        @DisplayName("거래처에 해당하는 문서들을 조회한다.")
        void getCompanyWithDocument() {
            CompanyResponseDTO company = createEntity("company", CompanyType.INPUT, "");

            PageRequest pageRequest = PageRequest.of(
                0,
                10,
                Sort.by(
                    new Sort.Order(Direction.ASC, "date"),
                    new Sort.Order(Direction.ASC, "createdAt")));

            ReadDocumentListResponseDTO companyDocument =
                companyService.getCompanyDocument(pageRequest, company.getCompanyId(), 2023, 1, false);

            assertThat(companyDocument.getDocumentList()).hasSize(0);
        }
    }

    @Nested
    @DisplayName("수정 테스트")
    class update {

        @Test
        @DisplayName("정상적으로 수정된다.")
        void updateCompany() {
            // given
            CompanyResponseDTO fakeCompany = createEntity("name", CompanyType.INPUT, "category");

            UpdateCompanyRequestDTO updateCompanyRequestDTO =
                new UpdateCompanyRequestDTO("updateName", "updateCategory", false);

            // when
            companyService.updateCompany(fakeCompany.getCompanyId(), updateCompanyRequestDTO);

            // then
            Company response = companyRepository.findById(fakeCompany.getCompanyId()).get();

            assertThat(response.getName()).isEqualTo("updateName");
            assertThat(response.getCategory()).isEqualTo("updateCategory");
            assertThat(response.isActive()).isFalse();
        }

        @Test
        @DisplayName("중복된 이름으로 수정을 시도한다면 실패한다.")
        void updateCompanyWithDuplicateName() {
            // given
            createEntity("name", CompanyType.INPUT, "category");
            CompanyResponseDTO companyResponseDTO = createEntity("duplicateName", CompanyType.INPUT, "category");

            UpdateCompanyRequestDTO updateCompanyRequestDTO =
                new UpdateCompanyRequestDTO("name", "category", true);

            // when
            // 인스턴스의 값은 업데이트 됐으나 db 에 save 되지 않은 상태라 바로 에러를 던지지 않음
            companyService.updateCompany(companyResponseDTO.getCompanyId(), updateCompanyRequestDTO);

            // then
            // entity 를 조회해서 db 를 update 시킬 때 에러가 발생한다.
            assertThatThrownBy(() -> companyRepository.findById(companyResponseDTO.getCompanyId()))
                .isInstanceOf(DataIntegrityViolationException.class);
        }

        @Test
        @DisplayName("여러 거래처의 활성화 상태를 일괄 수정한다.")
        void updateCompaniesActive() {
            CompanyResponseDTO company1 = createEntity("company1", CompanyType.INPUT, "");
            CompanyResponseDTO company2 = createEntity("company2", CompanyType.INPUT, "");

            UpdateCompanyActiveRequestDTO updateCompanyActiveRequestDTO =
                new UpdateCompanyActiveRequestDTO(List.of(
                    new UpdateCompanyActiveDTO(company1.getCompanyId(), false),
                    new UpdateCompanyActiveDTO(company2.getCompanyId(), false)));

            companyService.updateCompanyActive(updateCompanyActiveRequestDTO);

            assertThat(companyService.getCompany(company1.getCompanyId()).isActive()).isFalse();
            assertThat(companyService.getCompany(company2.getCompanyId()).isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("삭제 테스트")
    class delete {

        @Test
        @DisplayName("정상적으로 삭제된다.")
        void deleteCompany() {
            CompanyResponseDTO companyResponseDTO =
                createEntity("name", CompanyType.OUTPUT, "");

            companyService.deleteCompany(companyResponseDTO.getCompanyId());

            assertThatThrownBy(() -> companyService.getCompany(companyResponseDTO.getCompanyId()))
                .isInstanceOf(NotFoundException.class);
        }

        @Test
        @DisplayName("존재하지 않는 거래처의 삭제를 시도하면 실패한다.")
        void deleteCompanyByNotFoundId() {
            assertThatThrownBy(() -> companyService.deleteCompany(0L))
                .isInstanceOf(NotFoundException.class);
        }
    }

    CompanyResponseDTO createEntity(String name, CompanyType type, String category) {
        CreateCompanyRequestDTO createCompanyRequestDTO =
            new CreateCompanyRequestDTO(name, type.name(), category);

        return companyService.createCompany(createCompanyRequestDTO);
    }
}
