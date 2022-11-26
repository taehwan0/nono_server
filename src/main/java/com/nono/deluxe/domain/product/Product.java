package com.nono.deluxe.domain.product;

import com.nono.deluxe.controller.dto.product.UpdateProductRequestDTO;
import com.nono.deluxe.domain.imagefile.ImageFile;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(columnDefinition = "tinyint(1) default 0")
    private boolean deleted;

    @Builder
    public Product(String productCode, String name, String description, String category, String maker, String unit,
        StorageType storageType, String barcode, long stock, boolean active, ImageFile file, long price,
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
        this.active = active;
        this.file = file;
        this.price = price;
        this.margin = margin;
    }

    public void update(UpdateProductRequestDTO requestDTO) {
        this.productCode = requestDTO.getProductCode();
        this.name = requestDTO.getName();
        this.description = requestDTO.getDescription();
        this.category = requestDTO.getCategory();
        this.maker = requestDTO.getMaker();
        this.unit = requestDTO.getUnit();
        this.storageType = StorageType.valueOf(requestDTO.getStorageType().toUpperCase());
        this.barcode = requestDTO.getBarcode();
        this.stock = requestDTO.getStock();
        this.active = requestDTO.isActive();
        this.file = null; // requestDTO.getImage();
        this.price = requestDTO.getPrice();
        this.margin = requestDTO.getMargin();
    }

    public void updateStock(long stock) {
        this.stock = stock;
    }

    public void delete() {
        this.deleted = true;
        this.active = false;
        this.productCode = UUID.randomUUID().toString().substring(0, 18);
    }
}
