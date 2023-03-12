package com.nono.deluxe.document.presentation.dto.tempdocument;

import com.nono.deluxe.company.domain.Company;
import com.nono.deluxe.document.domain.DocumentType;
import com.nono.deluxe.document.domain.TempDocument;
import com.nono.deluxe.document.presentation.dto.record.RecordRequestDTO;
import com.nono.deluxe.user.domain.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateTempDocumentRequestDTO {

    // 거래 일자
    @NotNull
    LocalDate date;
    // 문서 유형 - TEMP_IMPUT / TEMP_OUTPUT
    @NotNull
    DocumentType type;

    @NotNull
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
