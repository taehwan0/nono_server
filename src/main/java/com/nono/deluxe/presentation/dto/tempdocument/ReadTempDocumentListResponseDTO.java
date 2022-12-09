package com.nono.deluxe.presentation.dto.tempdocument;

import com.nono.deluxe.domain.document.temp.TempDocument;
import com.nono.deluxe.presentation.dto.Meta;
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
