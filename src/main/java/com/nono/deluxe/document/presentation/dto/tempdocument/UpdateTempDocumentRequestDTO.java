package com.nono.deluxe.document.presentation.dto.tempdocument;

import com.nono.deluxe.document.domain.DocumentType;
import com.nono.deluxe.document.presentation.dto.record.RecordRequestDTO;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateTempDocumentRequestDTO {

    @NotNull
    LocalDate date;
    // 문서 유형 - TEMP_IMPUT / TEMP_OUTPUT
    @NotNull
    DocumentType type;

    //거래처 아이디
    @NotNull
    long companyId;

    // 거래 물품 기록 리스트.
    List<RecordRequestDTO> recordList = new ArrayList<>();
}
