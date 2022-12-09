package com.nono.deluxe.presentation.dto.record;

import com.nono.deluxe.domain.document.DocumentType;
import com.nono.deluxe.domain.record.Record;
import java.time.LocalDate;
import lombok.Data;

@Data
public class ProductRecordResponseDTO {

    private long recordId;
    private String writer;
    private long quantity;
    private long stock;
    private long price;
    private Long documentId;
    private DocumentType type;
    private LocalDate date;

    public ProductRecordResponseDTO(Record record) {
        this.recordId = record.getId();
        this.writer = record.getDocument().getWriter().getName();
        this.quantity = record.getQuantity();
        this.stock = record.getStock();
        this.price = record.getPrice();
        this.documentId = record.getDocument().getId();
        this.type = record.getDocument().getType();
        this.date = record.getDocument().getDate();
    }
}
