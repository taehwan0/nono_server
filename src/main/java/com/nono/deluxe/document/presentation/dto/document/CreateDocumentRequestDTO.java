package com.nono.deluxe.document.presentation.dto.document;

import com.nono.deluxe.company.domain.Company;
import com.nono.deluxe.document.domain.Document;
import com.nono.deluxe.document.domain.DocumentType;
import com.nono.deluxe.document.presentation.dto.record.RecordRequestDTO;
import com.nono.deluxe.user.domain.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateDocumentRequestDTO {

    @NotNull
    LocalDate date;
    @NotBlank
    String type;
    @NotNull
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
