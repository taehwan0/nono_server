package com.nono.deluxe.presentation;

import com.nono.deluxe.application.service.AuthService;
import com.nono.deluxe.application.service.CompanyService;
import com.nono.deluxe.presentation.dto.MessageResponseDTO;
import com.nono.deluxe.presentation.dto.company.CompanyListResponseDTO;
import com.nono.deluxe.presentation.dto.company.CompanyResponseDTO;
import com.nono.deluxe.presentation.dto.company.CreateCompanyRequestDTO;
import com.nono.deluxe.presentation.dto.company.UpdateCompanyActiveRequestDTO;
import com.nono.deluxe.presentation.dto.company.UpdateCompanyActiveResponseDTO;
import com.nono.deluxe.presentation.dto.company.UpdateCompanyRequestDTO;
import com.nono.deluxe.presentation.dto.document.ReadDocumentListResponseDTO;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/company")
@RestController
public class CompanyController {

    private final CompanyService companyService;
    private final AuthService authService;

    @PostMapping("")
    public ResponseEntity<CompanyResponseDTO> createCompany(
        @RequestHeader(name = "Authorization") String token,
        @Validated @RequestBody CreateCompanyRequestDTO createCompanyRequestDTO) {
        authService.validateTokenOverManagerRole(token);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(companyService.createCompany(createCompanyRequestDTO));
    }

    @GetMapping("")
    public ResponseEntity<CompanyListResponseDTO> getCompanyList(
        @RequestHeader(name = "Authorization") String token,
        @RequestParam(required = false, defaultValue = "") String query,
        @RequestParam(required = false, defaultValue = "name") String column,
        @RequestParam(required = false, defaultValue = "ASC") String order,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "false") boolean active) {
        authService.validateTokenOverParticipantRole(token);

        PageRequest pageRequest
            = PageRequest.of(page - 1, size, Sort.by(new Order(Direction.valueOf(order.toUpperCase()), column)));

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(companyService.getCompanyList(pageRequest, query, active));
    }

    @GetMapping("/input")
    public ResponseEntity<CompanyListResponseDTO> getInputCompanyList(
        @RequestHeader(name = "Authorization") String token,
        @RequestParam(required = false, defaultValue = "") String query,
        @RequestParam(required = false, defaultValue = "name") String column,
        @RequestParam(required = false, defaultValue = "ASC") String order,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "false") boolean active) {
        authService.validateTokenOverParticipantRole(token);

        PageRequest pageRequest
            = PageRequest.of(page - 1, size, Sort.by(new Order(Direction.valueOf(order.toUpperCase()), column)));

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(companyService.getInputCompanyList(pageRequest, query, active));
    }

    @GetMapping("/output")
    public ResponseEntity<CompanyListResponseDTO> getOutputCompanyList(
        @RequestHeader(name = "Authorization") String token,
        @RequestParam(required = false, defaultValue = "") String query,
        @RequestParam(required = false, defaultValue = "name") String column,
        @RequestParam(required = false, defaultValue = "ASC") String order,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "false") boolean active) {
        authService.validateTokenOverParticipantRole(token);

        PageRequest pageRequest
            = PageRequest.of(page - 1, size, Sort.by(new Order(Direction.valueOf(order.toUpperCase()), column)));

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(companyService.getOutputCompanyList(pageRequest, query, active));
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyResponseDTO> getCompanyById(
        @RequestHeader(name = "Authorization") String token,
        @PathVariable(name = "companyId") long companyId) {
        authService.validateTokenOverParticipantRole(token);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(companyService.getCompany(companyId));
    }

    @GetMapping("/{companyId}/document")
    public ResponseEntity<ReadDocumentListResponseDTO> getCompanyDocument(
        @RequestHeader(name = "Authorization") String token,
        @PathVariable(name = "companyId") long companyId,
        @RequestParam(required = false, defaultValue = "DESC") String order,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "0") int year,
        @RequestParam(required = false, defaultValue = "0") int month,
        @RequestParam(required = false, defaultValue = "true") boolean record) {
        authService.validateTokenOverParticipantRole(token);

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

    @PutMapping("/{companyId}")
    public ResponseEntity<CompanyResponseDTO> updateCompany(
        @RequestHeader(name = "Authorization") String token,
        @Validated @RequestBody UpdateCompanyRequestDTO updateCompanyRequestDTO,
        @PathVariable(name = "companyId") long companyId) {
        authService.validateTokenOverAdminRole(token);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(companyService.updateCompany(companyId, updateCompanyRequestDTO));
    }

    @PutMapping("/active")
    public ResponseEntity<UpdateCompanyActiveResponseDTO> updateCompanyActive(
        @RequestHeader(name = "Authorization") String token,
        @Validated @RequestBody UpdateCompanyActiveRequestDTO updateCompanyActiveRequestDTO) {
        authService.validateTokenOverAdminRole(token);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(companyService.updateCompanyActive(updateCompanyActiveRequestDTO));

    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<MessageResponseDTO> deleteCompany(@RequestHeader(name = "Authorization") String token,
        @PathVariable(name = "companyId") long companyId) {
        authService.validateTokenOverAdminRole(token);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(companyService.deleteCompany(companyId));
    }
}
