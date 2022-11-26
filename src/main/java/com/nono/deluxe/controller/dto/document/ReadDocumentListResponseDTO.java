package com.nono.deluxe.controller.dto.document;

import com.nono.deluxe.controller.dto.Meta;
import com.nono.deluxe.domain.document.Document;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.data.domain.Page;

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
            documentPage.getPageable().getPageNumber() + 1,
            documentPage.getNumberOfElements(),
            documentPage.getTotalPages(),
            documentPage.getTotalElements(),
            documentPage.isLast()
        );
    }
}
