package com.nono.deluxe.controller.dto.record;

import com.nono.deluxe.controller.dto.product.ProductResponseDTO;
import com.nono.deluxe.domain.document.DocumentType;
import com.nono.deluxe.domain.record.Record;
import com.nono.deluxe.domain.temprecord.TempRecord;
import lombok.Data;

@Data
public class RecordResponseDTO {

    private long recordId;
    private long quantity;
    private long stock;
    private long price;
    private DocumentType type;
    private ProductResponseDTO product;

    public RecordResponseDTO(Record record) {
        this.recordId = record.getId();
        this.quantity = record.getQuantity();
        this.stock = record.getStock();
        this.price = record.getPrice();
        this.type = record.getDocument().getType();
        this.product = new ProductResponseDTO(record.getProduct());
    }

    public RecordResponseDTO(TempRecord tempRecord) {
        this.recordId = tempRecord.getId();
        this.quantity = tempRecord.getQuantity();
        this.stock = 0; // Temp 기록은 저장된 양을 기록하지 않음.
        this.price = tempRecord.getPrice();
        this.product = new ProductResponseDTO(tempRecord.getProduct());
    }

}
