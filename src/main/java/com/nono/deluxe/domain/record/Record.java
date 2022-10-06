package com.nono.deluxe.domain.record;

import com.nono.deluxe.controller.dto.record.RecordRequestDTO;
import com.nono.deluxe.domain.document.Document;
import com.nono.deluxe.domain.product.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@Entity
public class Record {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false, foreignKey = @ForeignKey(name = "record_document"))
    private Document document;

    @NotBlank
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "record_product"))
    private Product product;

    @NotBlank
    @Min(0)
    @Column(nullable = false)
    private long quantity;

    @Column(nullable = false)
    private long stock;

    @Min(0)
    @Column(nullable = false)
    private long price;

    public void updateRecord(RecordRequestDTO requestDTO) {
        this.quantity = requestDTO.getQuantity();
        this.price = requestDTO.getPrice();
    }

    public void updateStock(long stock) {
        this.stock = stock;
    }

    @Builder
    public Record(Document document, Product product, long quantity, long stock, long price) {
        this.document = document;
        this.product = product;
        this.quantity = quantity;
        this.stock = stock;
        this.price = price;
    }
}
