package com.nono.deluxe.document.presentation.dto.tempdocument;

import com.nono.deluxe.common.presentation.dto.Meta;
import com.nono.deluxe.document.domain.TempDocument;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.data.domain.Page;

@Data
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
