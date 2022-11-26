package com.nono.deluxe.domain.record;

import com.nono.deluxe.controller.dto.record.RecordRequestDTO;
import com.nono.deluxe.domain.document.Document;
import com.nono.deluxe.domain.product.Product;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Record {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false, foreignKey = @ForeignKey(name = "record_document"))
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "record_product"))
    private Product product;

    @Column(nullable = false)
    private long quantity;

    @Column(nullable = false)
    private long stock;

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
