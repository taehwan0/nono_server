package com.nono.deluxe.domain.document;

import com.nono.deluxe.domain.BaseTimeEntity;
import com.nono.deluxe.domain.company.Company;
import com.nono.deluxe.domain.record.Record;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Document extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToMany(mappedBy = "document",
            fetch = FetchType.LAZY)
    List<Record> recordList = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "company_id")
    Company company;

}
