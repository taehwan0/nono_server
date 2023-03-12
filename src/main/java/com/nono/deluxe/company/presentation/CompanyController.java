package com.nono.deluxe.company.presentation;

import com.nono.deluxe.common.configuration.annotation.Auth;
import com.nono.deluxe.common.presentation.dto.MessageResponseDTO;
import com.nono.deluxe.company.application.CompanyService;
import com.nono.deluxe.company.presentation.dto.company.CompanyListResponseDTO;
import com.nono.deluxe.company.presentation.dto.company.CompanyResponseDTO;
import com.nono.deluxe.company.presentation.dto.company.CreateCompanyRequestDTO;
import com.nono.deluxe.company.presentation.dto.company.UpdateCompanyActiveRequestDTO;
import com.nono.deluxe.company.presentation.dto.company.UpdateCompanyActiveResponseDTO;
import com.nono.deluxe.company.presentation.dto.company.UpdateCompanyRequestDTO;
import com.nono.deluxe.document.presentation.dto.document.ReadDocumentListResponseDTO;
import com.nono.deluxe.user.domain.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/company")
@RestController
public class CompanyController {

    private final CompanyService companyService;

    @Auth(role = Role.ROLE_MANAGER)
    @PostMapping("")
    public ResponseEntity<CompanyResponseDTO> createCompany(
        @Validated @RequestBody CreateCompanyRequestDTO createCompanyRequestDTO) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(companyService.createCompany(createCompanyRequestDTO));
    }

    @Auth
    @GetMapping("")
    public ResponseEntity<CompanyListResponseDTO> getCompanyList(
        @RequestParam(required = false, defaultValue = "") String query,
        @RequestParam(required = false, defaultValue = "name") String column,
        @RequestParam(required = false, defaultValue = "ASC") String order,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "false") boolean active) {
        PageRequest pageRequest
            = PageRequest.of(page - 1, size, Sort.by(new Order(Direction.valueOf(order.toUpperCase()), column)));

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(companyService.getCompanyList(pageRequest, query, active));
    }

    @Auth
    @GetMapping("/input")
    public ResponseEntity<CompanyListResponseDTO> getInputCompanyList(
        @RequestParam(required = false, defaultValue = "") String query,
        @RequestParam(required = false, defaultValue = "name") String column,
        @RequestParam(required = false, defaultValue = "ASC") String order,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "false") boolean active) {
        PageRequest pageRequest
            = PageRequest.of(page - 1, size, Sort.by(new Order(Direction.valueOf(order.toUpperCase()), column)));

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(companyService.getInputCompanyList(pageRequest, query, active));
    }

    @Auth
    @GetMapping("/output")
    public ResponseEntity<CompanyListResponseDTO> getOutputCompanyList(
        @RequestParam(required = false, defaultValue = "") String query,
        @RequestParam(required = false, defaultValue = "name") String column,
        @RequestParam(required = false, defaultValue = "ASC") String order,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "false") boolean active) {
        PageRequest pageRequest
            = PageRequest.of(page - 1, size, Sort.by(new Order(Direction.valueOf(order.toUpperCase()), column)));

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(companyService.getOutputCompanyList(pageRequest, query, active));
    }

    @Auth
    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyResponseDTO> getCompanyById(@PathVariable(name = "companyId") long companyId) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(companyService.getCompany(companyId));
    }

    @Auth
    @GetMapping("/{companyId}/document")
    public ResponseEntity<ReadDocumentListResponseDTO> getCompanyDocument(
        @PathVariable(name = "companyId") long companyId,
        @RequestParam(required = false, defaultValue = "DESC") String order,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "0") int year,
        @RequestParam(required = false, defaultValue = "0") int month,
        @RequestParam(required = false, defaultValue = "true") boolean record) {
        PageRequest pageRequest = PageRequest.of(
            page - 1,
            size,
            Sort.by(
                new Sort.Order(Sort.Direction.valueOf(order.toUpperCase()), "date"),
                new Sort.Order(Sort.Direction.valueOf(order.toUpperCase()), "createdAt")));

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(companyService.getCompanyDocument(pageRequest, companyId, year, month, record));
    }

    @Auth(role = Role.ROLE_ADMIN)
    @PutMapping("/{companyId}")
    public ResponseEntity<CompanyResponseDTO> updateCompany(
        @Validated @RequestBody UpdateCompanyRequestDTO updateCompanyRequestDTO,
        @PathVariable(name = "companyId") long companyId) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(companyService.updateCompany(companyId, updateCompanyRequestDTO));
    }

    @Auth(role = Role.ROLE_ADMIN)
    @PutMapping("/active")
    public ResponseEntity<UpdateCompanyActiveResponseDTO> updateCompanyActive(
        @Validated @RequestBody UpdateCompanyActiveRequestDTO updateCompanyActiveRequestDTO) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(companyService.updateCompanyActive(updateCompanyActiveRequestDTO));

    }

    @Auth(role = Role.ROLE_ADMIN)
    @DeleteMapping("/{companyId}")
    public ResponseEntity<MessageResponseDTO> deleteCompany(@PathVariable(name = "companyId") long companyId) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(companyService.deleteCompany(companyId));
    }
}
