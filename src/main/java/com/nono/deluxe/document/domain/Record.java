package com.nono.deluxe.document.domain;

import com.nono.deluxe.document.domain.legacy.LegacyRecord;
import com.nono.deluxe.document.presentation.dto.record.RecordRequestDTO;
import com.nono.deluxe.product.domain.Product;
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
    private double price;

    @Builder
    public Record(Document document, Product product, long quantity, long stock, double price) {
        this.document = document;
        this.product = product;
        this.quantity = quantity;
        this.stock = stock;
        this.price = price;
    }

    private Record(Document document, Product product, LegacyRecord legacyRecord) {
        this.document = document;
        this.product = product;
        this.quantity = legacyRecord.getQuantity();
        this.stock = legacyRecord.getStock();

        if (document.getType().equals(DocumentType.INPUT)) {
            this.price = product.getInputPrice();
        } else {
            this.price = product.getOutputPrice();
        }
    }

    public static Record of(Document document, Product product, LegacyRecord legacyRecord) {
        return new Record(document, product, legacyRecord);
    }

    public void updateRecord(RecordRequestDTO requestDTO) {
        this.quantity = requestDTO.getQuantity();
        this.price = requestDTO.getPrice();
    }

    public void updateStock(long stock) {
        this.stock = stock;
    }
}
