package com.nono.deluxe.product.presentation.dto.product;

import com.nono.deluxe.document.domain.Record;
import com.nono.deluxe.document.presentation.dto.record.ProductRecordResponseDTO;
import com.nono.deluxe.product.domain.Product;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class GetRecordListResponseDTO extends ProductResponseDTO {

    // 입출고 기록
    private final List<ProductRecordResponseDTO> recordList;

    public GetRecordListResponseDTO(Product product, List<Record> recordList) {
        super(product);
        this.recordList = recordList.stream()
            .map(ProductRecordResponseDTO::new)
            .collect(Collectors.toList());
    }
}
