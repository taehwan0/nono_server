package com.nono.deluxe.domain.document.temp;

import com.nono.deluxe.controller.dto.tempdocument.UpdateTempDocumentRequestDTO;
import com.nono.deluxe.domain.BaseTimeEntity;
import com.nono.deluxe.domain.company.Company;
import com.nono.deluxe.domain.document.DocumentType;
import com.nono.deluxe.domain.temprecord.TempRecord;
import com.nono.deluxe.domain.user.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
public class TempDocument extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false, foreignKey = @ForeignKey(name = "temp_document_user"))
    private User writer;

    @OneToMany(mappedBy = "document",
        fetch = FetchType.LAZY)
    private List<TempRecord> documentItemList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false, foreignKey = @ForeignKey(name = "temp_document_company"))
    private Company company;

    @Builder
    public TempDocument(LocalDate date,
        DocumentType type,
        User writer,
        Company company) {
        this.date = date;
        this.type = type;
        this.writer = writer;
        this.company = company;
    }

    public void updateDocumentInfo(UpdateTempDocumentRequestDTO requestDto,
        User writer,
        Company company,
        List<TempRecord> updatedRecordList) {
        this.date = requestDto.getDate();
        this.type = requestDto.getType();
        this.company = company;
        this.writer = writer;
        this.documentItemList = updatedRecordList;
        setUpdatedAt(LocalDateTime.now());
    }
}
