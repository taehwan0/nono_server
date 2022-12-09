package com.nono.deluxe.presentation.dto.document;

import com.nono.deluxe.domain.document.Document;
import com.nono.deluxe.domain.document.DocumentType;
import com.nono.deluxe.domain.record.Record;
import com.nono.deluxe.presentation.dto.record.RecordResponseDTO;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class DocumentResponseDTO {

    long documentId;
    LocalDate date;
    DocumentType type;
    String companyName;
    String writer;
    long recordCount;
    long totalPrice;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    List<RecordResponseDTO> recordList = new ArrayList<>();

    public DocumentResponseDTO(Document document, long recordCount, long totalPrice, List<Record> recordList) {
        this.documentId = document.getId();
        this.date = document.getDate();
        this.type = document.getType();
        this.companyName = document.getCompany().getName();
        this.writer = document.getWriter().getName();
        this.recordCount = recordCount;
        this.totalPrice = totalPrice;
        this.createdAt = document.getCreatedAt();
        this.updatedAt = document.getUpdatedAt();

        for (Record record : recordList) {
            this.recordList.add(new RecordResponseDTO(record));
        }
    }
}
