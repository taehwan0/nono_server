package com.nono.deluxe.presentation;

import com.nono.deluxe.application.AuthService;
import com.nono.deluxe.application.CompanyService;
import com.nono.deluxe.presentation.dto.MessageResponseDTO;
import com.nono.deluxe.presentation.dto.company.CompanyResponseDTO;
import com.nono.deluxe.presentation.dto.company.CreateCompanyRequestDTO;
import com.nono.deluxe.presentation.dto.company.ReadCompanyListResponseDTO;
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

    /**
     * 필요권한: manager, admin
     *
     * @param token
     * @param requestDto
     * @return
     */
    @PostMapping("")
    public ResponseEntity<CompanyResponseDTO> createCompany(
        @RequestHeader(name = "Authorization") String token,
        @Validated @RequestBody CreateCompanyRequestDTO requestDto) {
        authService.validateManagerToken(token);

        CompanyResponseDTO responseDto = companyService.createCompany(requestDto);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    /**
     * 필요권한: participant, manager, admin
     *
     * @param token
     * @param query
     * @param column
     * @param order
     * @param size
     * @param page
     * @param active
     * @return
     */
    @GetMapping("")
    public ResponseEntity<ReadCompanyListResponseDTO> readCompanyList(
        @RequestHeader(name = "Authorization") String token,
        @RequestParam(required = false, defaultValue = "") String query,
        @RequestParam(required = false, defaultValue = "name") String column,
        @RequestParam(required = false, defaultValue = "ASC") String order,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "false") boolean active) {
        authService.validateParticipantToken(token);

        ReadCompanyListResponseDTO responseDto =
            companyService.readCompanyList(query, column, order, size, (page - 1), active);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    /**
     * 필요권한: participant, manager, admin
     *
     * @param token
     * @param companyId
     * @return
     */
    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyResponseDTO> readCompany(
        @RequestHeader(name = "Authorization") String token,
        @PathVariable(name = "companyId") long companyId) {
        authService.validateParticipantToken(token);

        CompanyResponseDTO responseDto = companyService.readCompany(companyId);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);

    }

    /**
     * 필요권한: participant, manager, admin
     *
     * @param token
     * @param companyId
     * @return
     */
    @GetMapping("/{companyId}/document")
    public ResponseEntity<ReadDocumentListResponseDTO> readDocumentList(
        @RequestHeader(name = "Authorization") String token,
        @PathVariable(name = "companyId") long companyId,
        @RequestParam(required = false, defaultValue = "DESC") String order,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "0") int year,
        @RequestParam(required = false, defaultValue = "0") int month) {
        authService.validateParticipantToken(token);

        ReadDocumentListResponseDTO responseDto =
            companyService.readCompanyDocument(companyId, order, size, (page - 1), year, month);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    /**
     * 필요권한: admin
     *
     * @param token
     * @param requestDto
     * @param companyId
     * @return
     */
    @PutMapping("/{companyId}")
    public ResponseEntity<CompanyResponseDTO> updateCompany(
        @RequestHeader(name = "Authorization") String token,
        @Validated @RequestBody UpdateCompanyRequestDTO requestDto,
        @PathVariable(name = "companyId") long companyId) {
        authService.validateAdminToken(token);

        CompanyResponseDTO responseDto = companyService.updateCompany(companyId, requestDto);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/active")
    public ResponseEntity<UpdateCompanyActiveResponseDTO> updateCompanyActive(
        @RequestHeader(name = "Authorization") String token,
        @Validated @RequestBody UpdateCompanyActiveRequestDTO requestDto) {
        authService.validateAdminToken(token);

        UpdateCompanyActiveResponseDTO responseDto = companyService.updateCompanyActive(requestDto);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);

    }

    /**
     * 필요권한: admin
     *
     * @param token
     * @param companyId
     * @return
     */
    @DeleteMapping("/{companyId}")
    public ResponseEntity<MessageResponseDTO> updateCompany(@RequestHeader(name = "Authorization") String token,
        @PathVariable(name = "companyId") long companyId) {
        authService.validateAdminToken(token);

        MessageResponseDTO responseDto = companyService.deleteCompany(companyId);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
