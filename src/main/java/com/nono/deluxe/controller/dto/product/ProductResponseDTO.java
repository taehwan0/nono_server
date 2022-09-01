package com.nono.deluxe.controller.dto.product;

import com.nono.deluxe.domain.product.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
/**
 * Create, Update, Read 에서 완전히 공통 데이터가 쓰인다면 이런 방법으로 해도 괜찮을 것 같습니다.
 * 다만 목록을 불러올 때는 최소한의 데이터를 불러오도록 하는게 맞을 것 같은데, (현재는 content 가 포함되고 있습니다만 content 를 65000 글자까지 지원하기 때문에, 데이터가 크지 않을까 우려됩니다.)
 * ex) 공지사항 목록 불러오기에서는 content 는 제외하고 title, focus, writer, createdAt, updatedAt 만 불러오도록 하는게 좋을까요?
 */

/**
 * from. hj.yang
 * 참고사항.
 * 일반적으로 API를 사용하는 입장에서는 응답을 줄 때 같은 의미를 가지는 데이터라면 같은 정보를 동일하게 내려주는 것이
 * 사용하는 사람의 입장에서 쉽게 처리할 수 있습니다.
 * Product를 의미하는 데이터라면 어디서 주든 같은 데이터로 내려준다면 받아서 사용할 때 프론트에서 같은 데이터 클래스를 사용하여
 * 처리하는 것이 가능해집니다.
 *
 * 만약 그렇지 못한 경우도 분명 존재하는데, 해당 부분이라면 상세 정보를 불러오는 API를 추가로 만듭니다.
 * 예를 들어 공지사항의 경우 목록에서는 해당 공지사항에 대한 메타데이터만 보여주고, getNoticeContents 혹은 getDetails라는 API를 통해
 * 전체 컨텐츠 데이터를 보여주는 부분이 사용하기에 가장 수월합니다.
 * (목록만 보는데 컨텐츠를 전부 다 보여줄 필요는 없으니..)
 * 만약 getNoticeContents API를 추가로 만든다면 해당 API에서는 꼭 필요한 데이터들 (Id / contents/ name정도 )만 넣어서 보내줍니다.
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
