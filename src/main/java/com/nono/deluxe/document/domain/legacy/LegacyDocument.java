package com.nono.deluxe.document.domain.legacy;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class LegacyDocument {

    @Id
    private Long docsCode;

    private String companyName;

    private int numOfPrd;

    private String docsType;

    private LocalDate date;

    private String writer;
}
