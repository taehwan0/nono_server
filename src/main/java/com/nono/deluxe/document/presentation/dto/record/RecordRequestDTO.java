package com.nono.deluxe.document.presentation.dto.record;

import com.nono.deluxe.document.domain.Document;
import com.nono.deluxe.document.domain.DocumentType;
import com.nono.deluxe.document.domain.Record;
import com.nono.deluxe.document.domain.TempDocument;
import com.nono.deluxe.document.domain.TempRecord;
import com.nono.deluxe.product.domain.Product;
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
    double price;

    public Record toEntity(Document document, Product product, long stock) {
        return Record.builder()
            .document(document)
            .product(product)
            .quantity(quantity)
            .price(price)
            .stock(stock)
            .build();
    }

    public TempRecord toTempEntity(TempDocument document, Product product) {
        // 입력 금액이 0원이면, product 의 기준 가격으로 대입함?
//        if (price == 0) {
//            price = getStandardPrice(document, product);
//        }

        return TempRecord.builder()
            .document(document)
            .product(product)
            .quantity(quantity)
            .price(price)
            .build();
    }

    private double getStandardPrice(TempDocument document, Product product) {
        if (document.getType().equals(DocumentType.INPUT)) {
            return product.getInputPrice();
        }
        return product.getOutputPrice();
    }
}
