package com.nono.deluxe.domain.product;

import com.nono.deluxe.domain.BaseTimeEntity;
import com.nono.deluxe.domain.record.Record;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Product extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String productCode;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String maker;

    @Column(nullable = false)
    private String standard;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Storage storage;

    @Column(nullable = true)
    private String barcode;

    @Column(nullable = false)
    private long stock;

    @Column(columnDefinition = "tinyint(1) default 1")
    private boolean onActivated;

    @Column(columnDefinition = "tinyint(1) default 0")
    private boolean isDeleted; // delete true -> join data, 이력용으로 쓰이며 외부로 노출되지 않음

    @Column(nullable = true)
    private String imageUri;

    @Column(nullable = false)
    private long price;

    @OneToMany(mappedBy = "product",
            fetch = FetchType.LAZY)
    private List<Record> recordList = new ArrayList<>();
}
