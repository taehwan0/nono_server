package com.nono.deluxe.controller.dto.document;

import com.nono.deluxe.controller.dto.record.RecordRequestDTO;
import com.nono.deluxe.domain.company.Company;
import com.nono.deluxe.domain.document.Document;
import com.nono.deluxe.domain.document.DocumentType;
import com.nono.deluxe.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class CreateDocumentRequestDTO {
    @NotBlank
    LocalDate date;
    @NotBlank
    String type;
    @NotBlank
    long companyId;

    List<RecordRequestDTO> recordList = new ArrayList<>();

    public Document toEntity(User writer, Company company) {
        return Document.builder()
                .date(this.date)
                .type(DocumentType.valueOf(this.type.toUpperCase()))
                .writer(writer)
                .company(company)
                .build();
    }
}
