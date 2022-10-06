package com.nono.deluxe.domain.document;

import com.nono.deluxe.domain.BaseTimeEntity;
import com.nono.deluxe.domain.company.Company;
import com.nono.deluxe.domain.record.Record;
import com.nono.deluxe.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
public class Document extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @Column(nullable = false)
    private LocalDate date;

    @NotBlank
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType type;

    @NotBlank
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false, foreignKey = @ForeignKey(name = "document_user"))
    private User writer;

    @NotBlank
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false, foreignKey = @ForeignKey(name = "document_company"))
    private Company company;

    @Builder
    public Document(LocalDate date, DocumentType type, User writer, Company company) {
        this.date = date;
        this.type = type;
        this.writer = writer;
        this.company = company;
    }

    public void updateCompany(Company company) {
        this.company = company;
    }
}
