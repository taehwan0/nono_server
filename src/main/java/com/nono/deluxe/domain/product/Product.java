package com.nono.deluxe.domain.product;

import com.nono.deluxe.domain.imagefile.ImageFile;
import com.nono.deluxe.domain.record.Record;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true, length = 20)
    private String productCode;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = true, length = 255)
    private String description;

    @Column(nullable = false, length = 30)
    private String category;

    @Column(nullable = false, length = 30)
    private String maker;

    // standard -> unit
    @Column(nullable = false, length = 30)
    private String unit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StorageType storageType;

    @Column(nullable = true, length = 50)
    private String barcode;

    @Column(nullable = false)
    private long stock;

    @Column(columnDefinition = "tinyint(1) default 1")
    private boolean active;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = true, foreignKey = @ForeignKey(name = "product_image"))
    private ImageFile file;

    @Column(nullable = true)
    private long price;

    @Column(nullable = true)
    private long margin;

    public void updateStock(long stock) {
        this.stock = stock;
    }
}
