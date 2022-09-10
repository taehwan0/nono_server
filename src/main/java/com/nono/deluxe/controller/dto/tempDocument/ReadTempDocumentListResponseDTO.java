package com.nono.deluxe.controller.dto.tempDocument;

import com.nono.deluxe.controller.dto.Meta;
import com.nono.deluxe.domain.document.temp.TempDocument;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class ReadTempDocumentListResponseDTO {
    private Meta meta;
    private List<TempDocumentInfoDTO> documentList = new ArrayList<>();

    public ReadTempDocumentListResponseDTO(Page<TempDocument> documentPage) {
        List<TempDocument> documentList = documentPage.getContent();
        documentList.forEach(tempDocument -> {
            this.documentList.add(new TempDocumentInfoDTO(tempDocument));
        });

        this.meta = new Meta(
                documentPage.getPageable().getPageNumber(),
                documentPage.getNumberOfElements(),
                documentPage.getTotalPages(),
                documentPage.getTotalElements(),
                documentPage.isLast()
        );
    }
}
