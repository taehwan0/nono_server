package com.nono.deluxe.domain.record;

import com.nono.deluxe.domain.BaseTimeEntity;
import com.nono.deluxe.domain.document.Document;
import com.nono.deluxe.domain.product.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Record extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private long quantity;

    @Column(nullable = false)
    private long stock;

    @Column(nullable = false)
    private long price;

}
