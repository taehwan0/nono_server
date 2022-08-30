package com.nono.deluxe.controller.dto.product;

import com.nono.deluxe.domain.product.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
/**
 * Create, Update, Read 에서 완전히 공통 데이터가 쓰인다면 이런 방법으로 해도 괜찮을 것 같습니다.
 * 다만 목록을 불러올 때는 최소한의 데이터를 불러오도록 하는게 맞을 것 같은데, (현재는 content 가 포함되고 있습니다만 content 를 65000 글자까지 지원하기 때문에, 데이터가 크지 않을까 우려됩니다.)
 * ex) 공지사항 목록 불러오기에서는 content 는 제외하고 title, focus, writer, createdAt, updatedAt 만 불러오도록 하는게 좋을까요?
 */
@NoArgsConstructor
@Getter
public class ProductResponseDTO {

    /// 상품 고유 아이디
    private long productId;
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
    private long stock;
    /// 기준 가격
    private long price;
    /// 마진율
    private long margin;
    /// 활성화 여부
    private boolean activate;
    // 이미지 데이터
    private String image;

    public ProductResponseDTO(Product product) {
        this.productId = product.getId();
        this.productCode = product.getProductCode();
        this.name = product.getName();
        this.description = product.getDescription();
        this.category = product.getCategory();
        this.maker = product.getMaker();
        this.unit = product.getUnit();
        this.storageType = product.getStorageType().name();
        this.barcode = product.getBarcode();
        this.stock = product.getStock();
        this.price = product.getPrice();
        this.margin = product.getMargin();
        this.activate = product.isActivate();
        //TODO: 이미지 파일 변환
        this.image = product.getFile().getUrl();
    }
}
