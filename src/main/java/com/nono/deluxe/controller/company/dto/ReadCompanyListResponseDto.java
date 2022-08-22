package com.nono.deluxe.controller.company.dto;

import com.nono.deluxe.domain.company.Company;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ReadCompanyListResponseDto {
    private Meta meta;
    private List<ReadCompanyResponseDto> companyList = new ArrayList<>();

    public ReadCompanyListResponseDto(long page, List<Company> companyList) {
        companyList.forEach(company -> {
            this.companyList.add(new ReadCompanyResponseDto(company));
        });
        this.meta = new Meta(page, this.companyList.size());
    }
}

class Meta {
    private long count;
    private long page;

    protected Meta(long page, long count) {
        this.page = page;
        this.count = count;
    }

    public long getCount() {
        return this.count;
    }

    public long getPage() {
        return this.page;
    }
}
