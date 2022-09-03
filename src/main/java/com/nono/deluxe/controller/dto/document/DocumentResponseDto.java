package com.nono.deluxe.controller.dto.document;

import com.nono.deluxe.controller.dto.record.RecordResponseDto;
import com.nono.deluxe.domain.document.Document;
import com.nono.deluxe.domain.document.DocumentType;
import com.nono.deluxe.domain.record.Record;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class DocumentResponseDto {
    long documentId;
    LocalDate date;
    DocumentType type;
    String companyName;
    String writer;
    long recordCount;
    long totalPrice;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    List<RecordResponseDto> recordList = new ArrayList<>();

    public DocumentResponseDto(Document document, long recordCount, long totalPrice, List<Record> recordList) {
        this.documentId = document.getId();
        this.date = document.getDate();
        this.type = document.getType();
        this.companyName = document.getCompany().getName();
        this.writer = document.getWriter().getName();
        this.recordCount = recordCount;
        this.totalPrice = totalPrice;
        this.createdAt = document.getCreatedAt();
        this.updatedAt = document.getUpdatedAt();

        for(Record record : recordList) {
            this.recordList.add(new RecordResponseDto(record));
        }
    }
}
