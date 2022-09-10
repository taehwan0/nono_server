package com.nono.deluxe.controller.dto.company;

import com.nono.deluxe.controller.dto.record.RecordResponseDTO;
import com.nono.deluxe.domain.company.Company;
import com.nono.deluxe.domain.company.CompanyType;
import com.nono.deluxe.domain.record.Record;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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
