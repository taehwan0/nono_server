package com.nono.deluxe.controller.dto.record;

import com.nono.deluxe.domain.document.Document;
import com.nono.deluxe.domain.product.Product;
import com.nono.deluxe.domain.record.Record;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class RecordRequestDTO {

    @NotBlank
    long productId;
    @NotBlank
    @Digits(integer = 10, fraction = 0)
    long quantity;
    @NotBlank
    @Digits(integer = 10, fraction = 0)
    long price;

    public Record toEntity(Document document, Product product, long stock) {
        return Record.builder()
                .document(document)
                .product(product)
                .quantity(this.quantity)
                .price(this.price)
                .stock(stock)
                .build();
    }
}
