package com.nono.deluxe.controller.dto.document;

import com.nono.deluxe.domain.document.Document;
import com.nono.deluxe.domain.document.DocumentType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class SimpleDocumentResponseDTO {

    long documentId;
    LocalDate date;
    DocumentType type;
    String companyName;
    String writer;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public SimpleDocumentResponseDTO(Document document) {
        this.documentId = document.getId();
        this.date = document.getDate();
        this.type = document.getType();
        this.companyName = document.getCompany().getName();
        this.writer = document.getWriter().getName();
        this.createdAt = document.getCreatedAt();
        this.updatedAt = document.getUpdatedAt();
    }
}
