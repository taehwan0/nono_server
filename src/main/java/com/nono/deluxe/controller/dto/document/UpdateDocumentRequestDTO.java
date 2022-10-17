package com.nono.deluxe.controller.dto.document;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.nono.deluxe.controller.dto.record.RecordRequestDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateDocumentRequestDTO {

	@NotNull
	long companyId; // 입출고처는 수정 가능하나 타 데이터 변화 가능성이 있음

	List<RecordRequestDTO> recordList = new ArrayList<>();
}
