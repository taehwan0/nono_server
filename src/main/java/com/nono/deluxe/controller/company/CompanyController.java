package com.nono.deluxe.controller.company;

import com.nono.deluxe.controller.company.dto.*;
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

    @PostMapping("/company")
    public ResponseEntity<CreateCompanyResponseDto> createCompany(@RequestHeader(name = "Authorization") String token,
                                                                  @Validated @RequestBody CreateCompanyRequestDto requestDto) {
        try {
            CreateCompanyResponseDto responseDto = companyService.createCompany(
                    requestDto.getName(),
                    requestDto.getType(),
                    requestDto.getCategory()
            );

            return ResponseEntity.status(HttpStatus.OK).body(responseDto);
        } catch (Exception e) {
            log.error(e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<ReadCompanyResponseDto> readCompany(@RequestHeader(name = "Authorization") String token,
                                                              @PathVariable(name = "companyId") long companyId) {
        try {
            ReadCompanyResponseDto responseDto = companyService.readCompany(companyId);

            return ResponseEntity.status(HttpStatus.OK).body(responseDto);
        } catch (Exception e) {
            log.error(e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/company/{companyId}")
    public ResponseEntity<UpdateCompanyResponseDto> updateCompany(@RequestHeader(name = "Authorization") String token,
                                                                  @Validated @RequestBody UpdateCompanyRequestDto requestDto,
                                                                  @PathVariable(name = "companyId") long companyId) {
        try {
            UpdateCompanyResponseDto responseDto = companyService.updateCompany(companyId,
                    requestDto.getName(),
                    requestDto.getType(),
                    requestDto.getCategory(),
                    requestDto.isActive()
            );

            return ResponseEntity.status(HttpStatus.OK).body(responseDto);
        } catch (Exception e) {
            log.error(e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/company/{companyId}")
    public ResponseEntity<DeleteCompanyResponseDto> updateCompany(@RequestHeader(name = "Authorization") String token,
                                                                  @PathVariable(name = "companyId") long companyId) {
        try {
            DeleteCompanyResponseDto responseDto = companyService.deleteCompany(companyId);

            return ResponseEntity.status(HttpStatus.OK).body(responseDto);
        } catch (Exception e) {
            log.error(e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
