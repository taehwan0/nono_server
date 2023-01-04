package com.nono.deluxe.domain.document;

import com.nono.deluxe.domain.BaseTimeEntity;
import com.nono.deluxe.domain.company.Company;
import com.nono.deluxe.domain.document.legacy.LegacyDocument;
import com.nono.deluxe.domain.record.Record;
import com.nono.deluxe.domain.user.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Document extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false, foreignKey = @ForeignKey(name = "document_user"))
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false, foreignKey = @ForeignKey(name = "document_company"))
    private Company company;

    @OneToMany(mappedBy = "document")
    List<Record> records = new ArrayList<>();

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

    public static Document of(LegacyDocument legacyDocument, User writer, Company company) {
        return new Document(
            legacyDocument.getDate(),
            DocumentType.valueOf(legacyDocument.getDocsType().toUpperCase()),
            writer,
            company);
    }
}
