package com.nono.deluxe.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.nono.deluxe.controller.dto.MessageResponseDTO;
import com.nono.deluxe.controller.dto.company.*;
import com.nono.deluxe.controller.dto.document.ReadDocumentListResponseDTO;
import com.nono.deluxe.service.AuthService;
import com.nono.deluxe.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
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
    @PostMapping("/company")
    public ResponseEntity<CompanyResponseDTO> createCompany(@RequestHeader(name = "Authorization") String token,
                                                            @Validated @RequestBody CreateCompanyRequestDTO requestDto) {
        DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
        if (authService.isManager(jwt) || authService.isAdmin(jwt)) {
        }
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
    @GetMapping("/company")
    public ResponseEntity<ReadCompanyListResponseDTO> readCompanyList(@RequestHeader(name = "Authorization") String token,
                                                                      @RequestParam(required = false, defaultValue = "") String query,
                                                                      @RequestParam(required = false, defaultValue = "name") String column,
                                                                      @RequestParam(required = false, defaultValue = "ASC") String order,
                                                                      @RequestParam(required = false, defaultValue = "10") int size,
                                                                      @RequestParam(required = false, defaultValue = "1") int page,
                                                                      @RequestParam(required = false, defaultValue = "false") boolean active) {
        DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
        if (authService.isParticipant(jwt) || authService.isManager(jwt) || authService.isAdmin(jwt)) {
        }
        ReadCompanyListResponseDTO responseDto = companyService.readCompanyList(query, column, order, size, (page - 1), active);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    /**
     * 필요권한: participant, manager, admin
     *
     * @param token
     * @param companyId
     * @return
     */
    @GetMapping("/company/{companyId}")
    public ResponseEntity<CompanyResponseDTO> readCompany(@RequestHeader(name = "Authorization") String token,
                                                          @PathVariable(name = "companyId") long companyId) {
        DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
        if (authService.isParticipant(jwt) || authService.isManager(jwt) || authService.isAdmin(jwt)) {
        }
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
    @GetMapping("/company/{companyId}/document")
    public ResponseEntity<ReadDocumentListResponseDTO> readDocumentList(@RequestHeader(name = "Authorization") String token,
                                                                        @PathVariable(name = "companyId") long companyId,
                                                                        @RequestParam(required = false, defaultValue = "DESC") String order,
                                                                        @RequestParam(required = false, defaultValue = "10") int size,
                                                                        @RequestParam(required = false, defaultValue = "1") int page,
                                                                        @RequestParam(required = false, defaultValue = "0") int year,
                                                                        @RequestParam(required = false, defaultValue = "0") int month) {
        DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
        if (authService.isParticipant(jwt) || authService.isManager(jwt) || authService.isAdmin(jwt)) {
        }
        ReadDocumentListResponseDTO responseDto = companyService.readCompanyDocument(companyId, order, size, (page - 1), year, month);

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
    @PutMapping("/company/{companyId}")
    public ResponseEntity<CompanyResponseDTO> updateCompany(@RequestHeader(name = "Authorization") String token,
                                                            @Validated @RequestBody UpdateCompanyRequestDTO requestDto,
                                                            @PathVariable(name = "companyId") long companyId) {
        DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
        if (authService.isAdmin(jwt)) {
        }
        CompanyResponseDTO responseDto = companyService.updateCompany(companyId, requestDto);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/company/active")
    public ResponseEntity<UpdateCompanyActiveResponseDTO> updateCompanyActive(@RequestHeader(name = "Authorization") String token,
                                                                              @Validated @RequestBody UpdateCompanyActiveRequestDTO requestDto) {
        DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
        if (authService.isAdmin(jwt)) {
        }
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
    @DeleteMapping("/company/{companyId}")
    public ResponseEntity<MessageResponseDTO> updateCompany(@RequestHeader(name = "Authorization") String token,
                                                            @PathVariable(name = "companyId") long companyId) {
        DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
        if (authService.isAdmin(jwt)) {
        }
        MessageResponseDTO responseDto = companyService.deleteCompany(companyId);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
