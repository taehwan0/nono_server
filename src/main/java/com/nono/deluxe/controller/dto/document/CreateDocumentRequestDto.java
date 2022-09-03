package com.nono.deluxe.controller.dto.document;

import com.nono.deluxe.controller.dto.record.RecordRequestDto;
import com.nono.deluxe.domain.company.Company;
import com.nono.deluxe.domain.document.Document;
import com.nono.deluxe.domain.document.DocumentType;
import com.nono.deluxe.domain.user.User;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class CreateDocumentRequestDto {
    LocalDate date;
    DocumentType type;
    long companyId;

    List<RecordRequestDto> recordList = new ArrayList<>();

    public Document toEntity(User writer, Company company) {
        return Document.builder()
                .date(this.date)
                .type(this.type)
                .writer(writer)
                .company(company)
                .build();
    }
}
