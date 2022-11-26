package com.nono.deluxe.controller.dto.product;

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

    /// 상품 코드
    @NotNull
    @Size(max = 20)
    private String productCode;
    /// 상품 이름
    @NotNull
    @Size(max = 30)
    private String name;
    /// 상품에 대한 설명
    private String description;
    /// 상품 분류
    @NotNull
    private String category;
    /// 제조사
    @NotNull
    @Size(max = 30)
    private String maker;
    /// 규격
    @NotNull
    @Size(max = 30)
    private String unit;
    /// 보관 방법 - Ice / Cold / Room
    @NotNull
    private String storageType;
    /// 바코드
    @Size(max = 50)
    private String barcode;
    /// 활성화 여부
    @NotNull
    private boolean active;
    /// 생성시 가지고 있는 재고.
    @NotNull
    @Min(0)
    private int stock;
    /// 기준 가격
    @Min(0)
    private int price;
    /// 마진율
    private int margin;
    // 이미지 데이터
    private String image;
}
