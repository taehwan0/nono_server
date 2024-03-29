package com.nono.deluxe.product.presentation.dto.product;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductRequestDTO {

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
    private boolean active;

    @NotNull
    @Min(0)
    private long stock;

    @Min(0)
    private double inputPrice;

    @Min(0)
    private double outputPrice;

    private double margin;

    private Long imageFileId;
}
