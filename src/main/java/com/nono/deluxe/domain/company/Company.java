package com.nono.deluxe.domain.company;

import com.nono.deluxe.domain.BaseTimeEntity;
import com.nono.deluxe.domain.document.Document;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Company extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompanyType type;

    @Column(nullable = true)
    private String category;

    @Column(columnDefinition = "tinyint(1) default 1")
    private boolean onActivated;

    @OneToMany(mappedBy = "company")
    List<Document> documentList = new ArrayList<>();
}
