package com.nono.deluxe.domain.tempdocument;

import com.nono.deluxe.domain.BaseTimeEntity;
import com.nono.deluxe.domain.company.Company;
import com.nono.deluxe.domain.record.Record;
import com.nono.deluxe.domain.temprecord.TempRecord;
import com.nono.deluxe.domain.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class TempDocument extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private User writer;

    @OneToMany(mappedBy = "document",
            fetch = FetchType.LAZY)
    private List<TempRecord> DocumentItemList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}
