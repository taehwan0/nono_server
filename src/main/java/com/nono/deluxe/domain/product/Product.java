package com.nono.deluxe.domain.product;

import com.nono.deluxe.domain.S3File.S3File;
import com.nono.deluxe.domain.record.Record;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String productCode;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String description;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String maker;

    @Column(nullable = false)
    private String standard;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StorageType storageType;

    @Column(nullable = true)
    private String barcode;

    @Column(nullable = false)
    private long stock;

    @Column(columnDefinition = "tinyint(1) default 1")
    private boolean activate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = true)
    private S3File file;

    // 미확정
    @Column(nullable = true)
    private long price;

    // 미확정
    @Column(nullable = true)
    private long margin;

    // 이런 방식으로 갈지 쿼리로 갈지
    // 해당 방식은 유용 할 수 있으나 헷갈림 -> 내부적으로는 쿼리 전송이랑 거의 비슷하거나 같음
    // 쿼리는 코드가 좀 더 쓰이겠지?
    @OneToMany(mappedBy = "product",
            fetch = FetchType.LAZY)
    private List<Record> recordList = new ArrayList<>();
}
