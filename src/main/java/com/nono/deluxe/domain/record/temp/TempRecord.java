package com.nono.deluxe.domain.record.temp;

import com.nono.deluxe.domain.BaseTimeEntity;
import com.nono.deluxe.domain.document.temp.TempDocument;
import com.nono.deluxe.domain.product.Product;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class TempRecord extends BaseTimeEntity {

    // 자동 생성되는 TempRecord ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // 해당 레코드에 관련된 temp 문서
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false, foreignKey = @ForeignKey(name = "temp_record_document"))
    private TempDocument document;

    // 해당 기록과 관련된 제품 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "temp_record_product"))
    private Product product;

    // 해당 기록의 입출고 수량
    @Column(nullable = false)
    private long quantity;

    // 개당 단가.
    @Column(nullable = false)
    private long price;

    @Builder
    public TempRecord(TempDocument document,
        Product product,
        long quantity,
        long price) {
        this.document = document;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }

    public void updatePrice(long price) {
        this.price = price;
    }
}
