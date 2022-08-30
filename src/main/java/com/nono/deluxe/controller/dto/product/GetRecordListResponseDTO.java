package com.nono.deluxe.controller.dto.product;

import com.nono.deluxe.domain.product.Product;
import com.nono.deluxe.domain.record.Record;

import java.util.List;

public class GetRecordListResponseDTO {
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
    // 입출고 기록
    private List<Record> recordList;

    public GetRecordListResponseDTO(Product product, List<Record> recordList) {
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
        this.recordList = recordList;
    }
}
