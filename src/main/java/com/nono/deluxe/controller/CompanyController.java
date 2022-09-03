package com.nono.deluxe.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.nono.deluxe.controller.dto.DeleteApiResponseDto;
import com.nono.deluxe.controller.dto.company.*;
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
     * @param token
     * @param requestDto
     * @return
     */
    @PostMapping("/company")
    public ResponseEntity<CreateCompanyResponseDto> createCompany(@RequestHeader(name = "Authorization") String token,
                                                                  @Validated @RequestBody CreateCompanyRequestDto requestDto) {
        try {
            DecodedJWT jwt = authService.decodeToken(token);
            if(authService.isManager(jwt) || authService.isAdmin(jwt)) {
                CreateCompanyResponseDto responseDto = companyService.createCompany(requestDto);

                return ResponseEntity.status(HttpStatus.OK).body(responseDto);
            } else {
                log.error("Company: forbidden create company {}", jwt.getClaim("userId"));

                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * 필요권한: participant, manager, admin
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
    public ResponseEntity<ReadCompanyListResponseDto> readCompanyList(@RequestHeader(name = "Authorization") String token,
                                                                      @RequestParam(required = false, defaultValue = "") String query,
                                                                      @RequestParam(required = false, defaultValue = "name") String column,
                                                                      @RequestParam(required = false, defaultValue = "ASC") String order,
                                                                      @RequestParam(required = false, defaultValue = "10") int size,
                                                                      @RequestParam(required = false, defaultValue = "0") int page,
                                                                      @RequestParam(required = false, defaultValue = "false") boolean active) {
        try {
            DecodedJWT jwt = authService.decodeToken(token);
            if(authService.isParticipant(jwt) || authService.isManager(jwt) || authService.isAdmin(jwt)) {
                ReadCompanyListResponseDto responseDto = companyService.readCompanyList(query, column, order, size, page, active);

                return ResponseEntity.status(HttpStatus.OK).body(responseDto);
            } else {
                log.error("Company: forbidden read company {}", jwt.getClaim("userId"));

                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * 필요권한: participant, manager, admin
     * @param token
     * @param companyId
     * @return
     */
    @GetMapping("/company/{companyId}")
    public ResponseEntity<ReadCompanyResponseDto> readCompany(@RequestHeader(name = "Authorization") String token,
                                                              @PathVariable(name = "companyId") long companyId) {
        try {
            DecodedJWT jwt = authService.decodeToken(token);
            if(authService.isParticipant(jwt) || authService.isManager(jwt) || authService.isAdmin(jwt)) {
                ReadCompanyResponseDto responseDto = companyService.readCompany(companyId);

                return ResponseEntity.status(HttpStatus.OK).body(responseDto);
            } else {
                log.error("Company: forbidden read company {}", jwt.getClaim("userId"));

                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * 필요권한: admin
     * @param token
     * @param requestDto
     * @param companyId
     * @return
     */
    @PutMapping("/company/{companyId}")
    public ResponseEntity<UpdateCompanyResponseDto> updateCompany(@RequestHeader(name = "Authorization") String token,
                                                                  @Validated @RequestBody UpdateCompanyRequestDto requestDto,
                                                                  @PathVariable(name = "companyId") long companyId) {
        try {
            DecodedJWT jwt = authService.decodeToken(token);
            if(authService.isAdmin(jwt)) {
                UpdateCompanyResponseDto responseDto = companyService.updateCompany(companyId, requestDto);

                return ResponseEntity.status(HttpStatus.OK).body(responseDto);
            } else {
                log.error("Company: forbidden update company {}", jwt.getClaim("userId"));

                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/company/active")
    public ResponseEntity<UpdateCompanyActiveResponseDto> updateCompanyActive(@RequestHeader(name = "Authorization") String token,
                                                                  @Validated @RequestBody UpdateCompanyActiveRequestDto requestDto) {
        try {
            DecodedJWT jwt = authService.decodeToken(token);
            if(authService.isAdmin(jwt)) {
                UpdateCompanyActiveResponseDto responseDto = companyService.updateCompanyActive(requestDto);

                return ResponseEntity.status(HttpStatus.OK).body(responseDto);
            } else {
                log.error("Company: forbidden update company {}", jwt.getClaim("userId"));

                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * 필요권한: admin
     * @param token
     * @param companyId
     * @return
     */
    @DeleteMapping("/company/{companyId}")
    public ResponseEntity<DeleteApiResponseDto> updateCompany(@RequestHeader(name = "Authorization") String token,
                                                              @PathVariable(name = "companyId") long companyId) {
        try {
            DecodedJWT jwt = authService.decodeToken(token);
            if(authService.isAdmin(jwt)) {
                DeleteApiResponseDto responseDto = companyService.deleteCompany(companyId);

                return ResponseEntity.status(HttpStatus.OK).body(responseDto);
            } else {
                log.error("Company: forbidden delete company {}", jwt.getClaim("userId"));

                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
