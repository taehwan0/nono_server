package com.nono.deluxe.controller.dto.notice;

import com.nono.deluxe.controller.dto.Meta;
import com.nono.deluxe.controller.dto.company.ReadCompanyResponseDto;
import com.nono.deluxe.domain.company.Company;
import com.nono.deluxe.domain.notice.Notice;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Data
public class ReadNoticeListResponseDto {
    private Meta meta;
    private List<ReadNoticeResponseDto> noticeList = new ArrayList<>();

    public ReadNoticeListResponseDto(Page<Notice> noticePage) {
        List<Notice> noticeList = noticePage.getContent();
        noticeList.forEach(notice -> {
            this.noticeList.add(new ReadNoticeResponseDto(notice));
        });
        noticePage.getTotalElements();
        noticePage.getTotalPages();
        noticePage.isLast();
        noticePage.getPageable().getPageNumber();
        this.meta = new Meta(
                noticePage.getPageable().getPageNumber(),
                noticePage.getNumberOfElements(),
                noticePage.getTotalPages(),
                noticePage.getTotalElements(),
                noticePage.isLast()
        );
    }
}
