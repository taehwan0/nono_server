package com.nono.deluxe.controller.dto.record;

import com.nono.deluxe.domain.document.Document;
import com.nono.deluxe.domain.document.temp.TempDocument;
import com.nono.deluxe.domain.product.Product;
import com.nono.deluxe.domain.record.Record;
import com.nono.deluxe.domain.temprecord.TempRecord;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecordRequestDTO {

    @NotNull
    long productId;
    @NotNull
    @Min(1)
    long quantity;
    @Min(0)
    long price = 0;

    public Record toEntity(Document document, Product product, long stock) {
        return Record.builder()
            .document(document)
            .product(product)
            .quantity(this.quantity)
            .price(this.price)
            .stock(stock)
            .build();
    }

    public TempRecord toTempEntity(TempDocument document, Product product) {
        long tempPrice = this.price;
        if (this.price <= 0) {
            tempPrice = product.getPrice();
        }
        return TempRecord.builder()
            .document(document)
            .product(product)
            .quantity(quantity)
            .price(tempPrice)
            .build();
    }
}
