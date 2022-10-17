package com.nono.deluxe.controller.dto.tempdocument;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.nono.deluxe.controller.dto.record.RecordRequestDTO;
import com.nono.deluxe.domain.document.DocumentType;

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
