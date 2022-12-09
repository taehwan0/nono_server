package com.nono.deluxe.presentation.dto.tempdocument;

import com.nono.deluxe.domain.company.Company;
import com.nono.deluxe.domain.document.DocumentType;
import com.nono.deluxe.domain.document.temp.TempDocument;
import com.nono.deluxe.domain.user.User;
import com.nono.deluxe.presentation.dto.record.RecordRequestDTO;
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
