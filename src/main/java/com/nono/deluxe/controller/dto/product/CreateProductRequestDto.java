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

/**
 * from. hj.yang
 * 1. requestDTO Validation 부분은 지난번에 이야기했던 @Validated 로 처리하는 부분에 문제가 있는지 확인 필요.
 * 해당 부분 문제 없으면 이렇게 진행해도 되지 않을까 싵은데..
 * 2. RequestDTO는 json 형태에서 자동 변환되서 들어오는 걸로 알고 있는데 enum으로 쓴 데이터를 그대로 받으면
 * 정확하게 맞지 않는 문자열이 들어오는 경우에는 오류가 발생하지 않을까 싶습니다만.. 해당 부분은 테스트하면서 체크해 봐도 좋을 것 같습니다.
 * 3. 이미지를 base64 endcode string 으로 처리할거라면 해당 방식이 맞습니다.
 * 궁금한 부분은 String으로 들어오는 바이너리 데이터라면 구지 해당 파일을 ImageFile 이라는 데이터클래스로 별도 저장할 필요가 있을까 싶습니다.
 * 그냥 product나 company등에 String으로 바로 넣어도 되지 않을까...?
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
        StorageType storage = StorageType.valueOf(storageType.toUpperCase());
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
