package com.nono.deluxe.company.presentation.dto.company;

import com.nono.deluxe.company.domain.Company;
import com.nono.deluxe.company.domain.CompanyType;
import com.nono.deluxe.document.domain.Record;
import com.nono.deluxe.document.presentation.dto.record.RecordResponseDTO;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class CompanyRecordResponseDTO {

    private long companyId;
    private String name;
    private CompanyType type;
    private String category;
    private boolean active;

    private List<RecordResponseDTO> recordList = new ArrayList<>();

    public CompanyRecordResponseDTO(Company company, List<Record> recordList) {
        this.companyId = company.getId();
        this.name = company.getName();
        this.type = company.getType();
        this.category = company.getCategory();
        this.active = company.isActive();

        for (Record record : recordList) {
            this.recordList.add(new RecordResponseDTO(record));
        }
    }
}
