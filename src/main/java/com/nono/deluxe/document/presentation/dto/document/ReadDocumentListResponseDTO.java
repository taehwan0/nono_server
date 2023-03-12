package com.nono.deluxe.document.presentation.dto.document;

import com.nono.deluxe.common.presentation.dto.Meta;
import com.nono.deluxe.document.domain.Document;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class ReadDocumentListResponseDTO {

    private Meta meta;
    private List<DocumentResponseDTO> documentList = new ArrayList<>();

    public ReadDocumentListResponseDTO(Page<Document> documentPage, boolean withRecord) {
        List<Document> documentList = documentPage.getContent();
        documentList.forEach(document -> {
            this.documentList.add(new DocumentResponseDTO(document, withRecord));
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
