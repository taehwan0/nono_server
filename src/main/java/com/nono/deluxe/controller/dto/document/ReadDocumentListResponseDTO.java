package com.nono.deluxe.controller.dto.document;

import com.nono.deluxe.controller.dto.Meta;
import com.nono.deluxe.domain.document.Document;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Data
public class ReadDocumentListResponseDTO {
    private Meta meta;
    private List<SimpleDocumentResponseDTO> documentList = new ArrayList<>();

    public ReadDocumentListResponseDTO(Page<Document> documentPage) {
        List<Document> documentList = documentPage.getContent();
        documentList.forEach(document -> {
            this.documentList.add(new SimpleDocumentResponseDTO(document));
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
