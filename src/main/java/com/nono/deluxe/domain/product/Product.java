package com.nono.deluxe.domain.product;

import com.nono.deluxe.domain.imagefile.ImageFile;
import com.nono.deluxe.domain.record.Record;
import lombok.Builder;
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

    // 이런 방식으로 갈지 쿼리로 갈지
    // 해당 방식은 유용 할 수 있으나 헷갈림 -> 내부적으로는 쿼리 전송이랑 거의 비슷하거나 같음
    // 쿼리는 코드가 좀 더 쓰이겠지?
    @OneToMany(mappedBy = "product",
            fetch = FetchType.LAZY)
    private List<Record> recordList = new ArrayList<>();

    @Builder
    public Product(String productCode, String name, String description, String category, String maker, String unit, StorageType storageType, String barcode, long stock, boolean activate, ImageFile file, long price, long margin) {
        this.productCode = productCode;
        this.name = name;
        this.description = description;
        this.category = category;
        this.maker = maker;
        this.unit = unit;
        this.storageType = storageType;
        this.barcode = barcode;
        this.stock = stock;
        this.activate = activate;
        this.file = file;
        this.price = price;
        this.margin = margin;
    }

    public void update(String productCode,
                       String name,
                       String description,
                       String category,
                       String maker,
                       String unit,
                       StorageType storageType,
                       String barcode,
                       long stock,
                       boolean activate,
                       ImageFile file,
                       long price,
                       long margin) {
        this.productCode = productCode;
        this.name = name;
        this.description = description;
        this.category = category;
        this.maker = maker;
        this.unit = unit;
        this.storageType = storageType;
        this.barcode = barcode;
        this.stock = stock;
        this.activate = activate;
        this.file = file;
        this.price = price;
        this.margin = margin;
        
    public void updateStock(long stock) {
        this.stock = stock;
    }
}
