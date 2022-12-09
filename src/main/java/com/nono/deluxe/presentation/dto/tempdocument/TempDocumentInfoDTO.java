package com.nono.deluxe.presentation.dto.tempdocument;

import com.nono.deluxe.domain.document.DocumentType;
import com.nono.deluxe.domain.document.temp.TempDocument;
import com.nono.deluxe.domain.record.temp.TempRecord;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class TempDocumentInfoDTO {

    // 임시 Document 의 id
    long documentId;
    // 거래 날짜
    LocalDate date;
    // 문서 종료 - INPUT / OUTPUT
    DocumentType type;
    // 거래처 이름
    String companyName;
    // 작성자
    String writer;
    // 해당 문서에 기록한 리스트 개수
    long recordCount;
    // 전체 리스트 계산 총합
    long totalPrice;
    // 문서 생성 일시
    LocalDateTime createdAt;
    // 문서 수정 일시
    LocalDateTime updatedAt;

    public TempDocumentInfoDTO(TempDocument document) {
        this.documentId = document.getId();
        this.date = document.getDate();
        this.type = document.getType();
        this.companyName = document.getCompany().getName();
        this.writer = document.getWriter().getName();
        List<TempRecord> recordList = document.getDocumentItemList();
        this.recordCount = recordList.size();
        long totalPrice = 0;
        for (TempRecord record : recordList) {
            totalPrice += record.getPrice() * record.getQuantity();
        }
        this.totalPrice = totalPrice;
        this.createdAt = document.getCreatedAt();
        this.updatedAt = document.getUpdatedAt();
    }
}
