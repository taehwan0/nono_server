package com.nono.deluxe.document.presentation.dto.document;

import com.nono.deluxe.document.presentation.dto.record.RecordRequestDTO;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateDocumentRequestDTO {

    @NotNull
    long companyId; // 입출고처는 수정 가능하나 타 데이터 변화 가능성이 있음

    List<RecordRequestDTO> recordList = new ArrayList<>();
}
