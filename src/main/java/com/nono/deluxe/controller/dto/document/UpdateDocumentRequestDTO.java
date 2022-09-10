package com.nono.deluxe.controller.dto.document;

import com.nono.deluxe.controller.dto.record.RecordRequestDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateDocumentRequestDTO {

    long companyId; // 입출고처는 수정 가능하나 타 데이터 변화 가능성이 있음

    List<RecordRequestDTO> recordList = new ArrayList<>();
}
