package com.nono.deluxe.controller.dto.product;

import com.nono.deluxe.domain.product.Product;
import com.nono.deluxe.domain.product.StorageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * RequestDto 에 전부 Validation 적용이 필요한데 이 부분은 회의를 거치고 할 필요가 있는 것 같습니다.
 * storageType 은 enum 으로 만든 데이터를 사용하려고 합니다.
 * 이미지 데이터는 프론트에서 확장자, 크기 검증 후 base64 인코딩, String 으로 변환되어 이곳으로 와서 Service 에서 처리.. 의 프로세스가 맞을까요?
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductRequestDto {

    /// 상품 코드
    private String productCode;
    /// 상품 이름
    private String name;
    /// 상품에 대한 설명
    private String description;
    /// 상품 분류
    private String category;
    /// 제조사
    private String maker;
    /// 규격
    private String unit;
    /// 보관 방법 - Ice / Cold / Room
    private String storageType;
    /// 바코드
    private String barcode;
    /// 생성시 가지고 있는 재고.
    private int stock;
    /// 기준 가격
    private int price;
    /// 마진율
    private int margin;
    // 이미지 데이터
    private String image;


    public Product toEntity() {
        StorageType storage = StorageType.valueOf(storageType);
        return Product.builder()
                .productCode(productCode)
                .name(name)
                .description(description)
                .category(category)
                .maker(maker)
                .unit(unit).
                storageType(storage).
                barcode(barcode).
                stock(stock).
                price(price).
                margin(margin)
                .build();
    }
}
