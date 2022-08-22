package com.nono.deluxe.domain.company;

import com.nono.deluxe.domain.document.Document;
import lombok.Builder;
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

    public void update(String name, CompanyType type, String category, boolean active) {
        this.name = name;
        this.type = type;
        this.category = category;
        this.activate = active;
    }


    @Builder
    public Company(String name, CompanyType type, String category) {
        this.name = name;
        this.type = type;
        this.category = category;
        this.activate = true; // 적용이 안되서 일단 넣음.
    }
}
