package com.nono.deluxe.controller.dto.product;

import com.nono.deluxe.controller.dto.Meta;
import com.nono.deluxe.domain.product.Product;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * C R U D 라고 하니까 여지껏 이름을
 * Create, Read, Update, Delete 로 지어왔는데 메소드에서는 getXXX 로 많이 사용하기 때문에 get 도 맞지 않을까 하는 생각이 들게 합니다.
 * 어느쪽이던 통일이 필요할 것 같습니다. dto 또한 DTO, Dto 한쪽 타입으로 통일이 필요합니다.
 */

/**
 * from. hj.yang
 * 해당 부분의 경우 개인적인 코딩 컨벤션인 경우가 많습니다.
 * 저는 함수 API가 GetXXX라고 만들어질 경우 해당 함수가 의미있는 함수로 분기되지 않을 경우 해당 이름을 그대로 활용하여
 * 처음 시작한 부분과 거리감을 두지 않기 위해 해당 부분에 대하여 만든 부분입니다.
 * ex) API이름이 GetProductList -> DTO이름, Service 이름, 등 비슷하거나 동일한 의미가 사용되는 부분이라면 하나로 통일하고 있습니다.
 *    -> GetProductList -> 내부에서는 ReadProductList 하면 혼동이 올 수 있음.
 *
 * 해당 부분 참고하셔서 통일 방안 확인 부탁드립니다.
 */
@Getter
@Data
public class GetProductListResponseDTO {
    private Meta meta;
    private List<ProductResponseDTO> productList;

    public GetProductListResponseDTO(Page<Product> productPage) {
        this.meta = new Meta(productPage.getNumber(),
                productPage.getNumberOfElements(),
                productPage.getTotalPages(),
                productPage.getTotalElements(),
                productPage.isLast());

        List<Product> productList = productPage.getContent();
        productList.forEach(product -> {
            this.productList.add(new ProductResponseDTO(product));
        });
    }
}

