package com.nono.deluxe.company.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, length = 30)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompanyType type;

    @Column(nullable = true, length = 30)
    private String category;

    @Column(columnDefinition = "tinyint(1) default 1")
    private boolean active;

    @Column(columnDefinition = "tinyint(1) default 0")
    private boolean deleted;

    public void update(String name, String category, boolean active) {
        this.name = name;
        this.category = category;
        this.active = active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Builder
    public Company(String name, CompanyType type, String category) {
        this.name = name;
        this.type = type;
        this.category = category;
        this.active = true; // 적용이 안되서 일단 넣음.
    }

    public void delete() {
        this.deleted = true;
        this.active = false;
        this.name = null;
    }
}
