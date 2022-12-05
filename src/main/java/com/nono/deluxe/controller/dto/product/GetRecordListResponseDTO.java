package com.nono.deluxe.controller.dto.product;

import com.nono.deluxe.controller.dto.record.ProductRecordResponseDTO;
import com.nono.deluxe.domain.product.Product;
import com.nono.deluxe.domain.record.Record;
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
