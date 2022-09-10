package com.nono.deluxe.controller.dto.tempDocument;

import com.nono.deluxe.controller.dto.record.RecordRequestDTO;
import com.nono.deluxe.domain.document.DocumentType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateTempDocumentRequestDTO {
    LocalDate date;
    // 문서 유형 - TEMP_IMPUT / TEMP_OUTPUT
    DocumentType type;

    //거래처 아이디
    long companyId;

    // 거래 물품 기록 리스트.
    List<RecordRequestDTO> recordList = new ArrayList<>();
}
