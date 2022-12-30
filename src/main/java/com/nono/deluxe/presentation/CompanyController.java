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
        @Validated @RequestBody CreateCompanyRequestDTO requestDto) {
        authService.validateTokenOverManagerRole(token);

        CompanyResponseDTO responseDto = companyService.createCompany(requestDto);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
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

        CompanyListResponseDTO responseDto =
            companyService.getCompanyList(query, column, order, size, (page - 1), active);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
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

        CompanyListResponseDTO responseDto =
            companyService.getInputCompanyList(query, column, order, size, (page - 1), active);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
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

        CompanyListResponseDTO responseDto =
            companyService.getOutputCompanyList(query, column, order, size, (page - 1), active);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyResponseDTO> getCompany(
        @RequestHeader(name = "Authorization") String token,
        @PathVariable(name = "companyId") long companyId) {
        authService.validateTokenOverParticipantRole(token);

        CompanyResponseDTO responseDto = companyService.getCompany(companyId);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);

    }

    @GetMapping("/{companyId}/document")
    public ResponseEntity<ReadDocumentListResponseDTO> getCompanyDocument(
        @RequestHeader(name = "Authorization") String token,
        @PathVariable(name = "companyId") long companyId,
        @RequestParam(required = false, defaultValue = "DESC") String order,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "0") int year,
        @RequestParam(required = false, defaultValue = "0") int month) {
        authService.validateTokenOverParticipantRole(token);

        ReadDocumentListResponseDTO responseDto =
            companyService.getCompanyDocument(companyId, order, size, (page - 1), year, month);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/{companyId}")
    public ResponseEntity<CompanyResponseDTO> updateCompany(
        @RequestHeader(name = "Authorization") String token,
        @Validated @RequestBody UpdateCompanyRequestDTO requestDto,
        @PathVariable(name = "companyId") long companyId) {
        authService.validateTokenOverAdminRole(token);

        CompanyResponseDTO responseDto = companyService.updateCompany(companyId, requestDto);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/active")
    public ResponseEntity<UpdateCompanyActiveResponseDTO> updateCompanyActive(
        @RequestHeader(name = "Authorization") String token,
        @Validated @RequestBody UpdateCompanyActiveRequestDTO requestDto) {
        authService.validateTokenOverAdminRole(token);

        UpdateCompanyActiveResponseDTO responseDto = companyService.updateCompanyActive(requestDto);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);

    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<MessageResponseDTO> deleteCompany(@RequestHeader(name = "Authorization") String token,
        @PathVariable(name = "companyId") long companyId) {
        authService.validateTokenOverAdminRole(token);

        MessageResponseDTO responseDto = companyService.deleteCompany(companyId);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
