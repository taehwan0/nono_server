package com.nono.deluxe.controller.dto.document;

import com.nono.deluxe.domain.document.Document;
import com.nono.deluxe.domain.document.DocumentType;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class DocumentResponseDto {
    long documentId;
    LocalDate date;
    DocumentType type;
    String companyName;
    String writer;
    int recordCount;
    long totalPrice;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public DocumentResponseDto(Document document, int recordCount, long totalPrice) {
        this.documentId = document.getId();
        this.date = document.getDate();
        this.type = document.getType();
        this.companyName = document.getCompany().getName();
        this.writer = document.getWriter().getName();
        this.recordCount = recordCount;
        this.totalPrice = totalPrice;
        this.createdAt = document.getCreatedAt();
        this.updatedAt = document.getUpdatedAt();
    }
}
