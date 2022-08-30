package com.nono.deluxe.controller.dto.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetProductListResponseDTO {
    private int count;
    private int nextPage;
    private List<ProductResponseDTO> productList;
}

