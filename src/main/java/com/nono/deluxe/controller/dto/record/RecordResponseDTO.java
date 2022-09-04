package com.nono.deluxe.controller.dto.record;

import com.nono.deluxe.controller.dto.product.ProductResponseDTO;
import com.nono.deluxe.domain.record.Record;
import lombok.Data;

@Data
public class RecordResponseDTO {

    private long recordId;
    private long quantity;
    private long stock;
    private long price;
    private ProductResponseDTO product;

    public RecordResponseDTO(Record record) {
        this.recordId = record.getId();
        this.quantity = record.getQuantity();
        this.stock = record.getStock();
        this.price = record.getPrice();
        this.product = new ProductResponseDTO(record.getProduct());
    }

}
