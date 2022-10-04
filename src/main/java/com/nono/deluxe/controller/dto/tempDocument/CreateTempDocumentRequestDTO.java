package com.nono.deluxe.controller.dto.tempDocument;

import com.nono.deluxe.controller.dto.record.RecordRequestDTO;
import com.nono.deluxe.domain.company.Company;
import com.nono.deluxe.domain.document.DocumentType;
import com.nono.deluxe.domain.document.temp.TempDocument;
import com.nono.deluxe.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class CreateTempDocumentRequestDTO {
    // 거래 일자
    @NotBlank
    LocalDate date;
    // 문서 유형 - TEMP_IMPUT / TEMP_OUTPUT
    @NotBlank
    DocumentType type;

    @NotBlank
    long companyId;

    List<RecordRequestDTO> recordList = new ArrayList<>();

    public TempDocument toEntity(User writer, Company company) {
        return TempDocument.builder()
                .date(this.date)
                .type(this.type)
                .writer(writer)
                .company(company)
                .build();
    }
}
