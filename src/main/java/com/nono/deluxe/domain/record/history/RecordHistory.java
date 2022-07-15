package com.nono.deluxe.domain.record.history;

import com.nono.deluxe.domain.BaseTimeEntity;
import com.nono.deluxe.domain.document.Document;
import com.nono.deluxe.domain.product.Product;
import com.nono.deluxe.domain.record.Record;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class RecordHistory extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id", nullable = false)
    private Record record;

    @Column(nullable = false)
    private long quantity;

    @Column(nullable = false)
    private long stock;

    @Column(nullable = false)
    private long price;
}
