package com.nono.deluxe.controller.dto.product;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
/**
 * C R U D 라고 하니까 여지껏 이름을
 * Create, Read, Update, Delete 로 지어왔는데 메소드에서는 getXXX 로 많이 사용하기 때문에 get 도 맞지 않을까 하는 생각이 들게 합니다.
 * 어느쪽이던 통일이 필요할 것 같습니다. dto 또한 DTO, Dto 한쪽 타입으로 통일이 필요합니다.
 */
@Getter
@AllArgsConstructor
public class GetProductListResponseDTO {

    private int count;
    private int nextPage;
    private List<ProductResponseDTO> productList;
}

