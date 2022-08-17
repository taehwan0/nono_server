package com.nono.deluxe.domain.company;

import com.nono.deluxe.domain.document.Document;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Company {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true, length = 30)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompanyType type;

    @Column(nullable = true, length = 30)
    private String category;

    @Column(columnDefinition = "tinyint(1) default 1")
    private boolean activate;

    /**
     * 사용 될라나..? -> Company 별 Document 모아보기
     */
    @OneToMany(mappedBy = "company", fetch = FetchType.LAZY)
    List<Document> documentList = new ArrayList<>();
}
