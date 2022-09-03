package com.nono.deluxe.controller.dto.document;

import com.nono.deluxe.controller.dto.record.RecordRequestDto;
import com.nono.deluxe.domain.company.Company;
import com.nono.deluxe.domain.document.Document;
import com.nono.deluxe.domain.document.DocumentType;
import com.nono.deluxe.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateDocumentRequestDto {

    long companyId; // 입출고처는 수정 가능하나 타 데이터 변화 가능성이 있음

    List<RecordRequestDto> recordList = new ArrayList<>();
}
