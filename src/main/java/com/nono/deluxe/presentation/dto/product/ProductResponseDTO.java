package com.nono.deluxe.presentation.dto.product;

import com.nono.deluxe.domain.product.Product;
import com.nono.deluxe.domain.product.StorageType;
import com.nono.deluxe.presentation.dto.imagefile.ImageFileResponseDTO;
import lombok.Data;

@Data
public class ProductResponseDTO {

    private Long productId;
    private String productCode;
    private String name;
    private String description;
    private String category;
    private String maker;
    private String unit;
    private StorageType storageType;
    private String barcode;
    private long stock;
    private double inputPrice;
    private double outputPrice;
    private double margin;
    private boolean active;
    private ImageFileResponseDTO image;

    public ProductResponseDTO(Product product) {
        this.productId = product.getId();
        this.productCode = product.getProductCode();
        this.name = product.getName();
        this.description = product.getDescription();
        this.category = product.getCategory();
        this.maker = product.getMaker();
        this.unit = product.getUnit();
        this.storageType = product.getStorageType();
        this.barcode = product.getBarcode();
        this.stock = product.getStock();
        this.inputPrice = product.getInputPrice();
        this.outputPrice = product.getOutputPrice();
        this.margin = product.getMargin();
        this.active = product.isActive();

        if (product.getFile() == null) {
            this.image = new ImageFileResponseDTO();
        } else {
            this.image = new ImageFileResponseDTO(product.getFile());
        }
    }
}
