package com.nono.deluxe.document.presentation.dto.record;

import com.nono.deluxe.document.domain.DocumentType;
import com.nono.deluxe.document.domain.Record;
import java.time.LocalDate;
import lombok.Data;

@Data
public class ProductRecordResponseDTO {

    private long recordId;
    private String writer;
    private long quantity;
    private long stock;
    private double price;
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
