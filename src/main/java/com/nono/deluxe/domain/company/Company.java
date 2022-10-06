package com.nono.deluxe.domain.company;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
public class Company {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @Size(max = 30)
    @Column(nullable = false, unique = true, length = 30)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompanyType type;

    @Size(max = 30)
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
        this.name = UUID.randomUUID().toString().substring(0, 18);
    }
}
