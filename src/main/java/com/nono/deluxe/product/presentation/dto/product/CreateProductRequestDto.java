package com.nono.deluxe.product.presentation.dto.product;

import com.nono.deluxe.product.domain.Product;
import com.nono.deluxe.product.domain.StorageType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductRequestDto {

    @NotNull
    @Size(max = 20)
    private String productCode;

    @NotNull
    @Size(max = 30)
    private String name;

    private String description;

    @NotNull
    private String category;

    @NotNull
    @Size(max = 30)
    private String maker;

    @NotNull
    @Size(max = 30)
    private String unit;

    @NotNull
    private String storageType;

    @Size(max = 50)
    private String barcode;

    @Size(max = 50)
    private String barcodeType;

    @NotNull
    @Min(0)
    private long stock;

    @Min(0)
    private double inputPrice;

    @Min(0)
    private double outputPrice;

    private double margin;

    private Long imageFileId;

    public Product toEntity() {
        return Product.builder()
            .productCode(productCode)
            .name(name)
            .description(description)
            .category(category)
            .maker(maker)
            .unit(unit)
            .storageType(StorageType.valueOf(storageType.toUpperCase()))
            .barcode(barcode)
            .barcodeType(barcodeType)
            .stock(stock)
            .inputPrice(inputPrice)
            .outputPrice(outputPrice)
            .margin(margin)
            .build();
    }
}
