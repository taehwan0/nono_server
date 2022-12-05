package com.nono.deluxe.controller.dto.product;

import com.nono.deluxe.controller.dto.Meta;
import com.nono.deluxe.domain.product.Product;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.domain.Page;

/**
 * from. hj.yang 해당 부분의 경우 개인적인 코딩 컨벤션인 경우가 많습니다. 저는 함수 API 가 GetXXX 라고 만들어질 경우 해당 함수가 의미있는 함수로 분기되지 않을 경우 해당 이름을 그대로
 * 활용하여 처음 시작한 부분과 거리감을 두지 않기 위해 해당 부분에 대하여 만든 부분입니다. ex) API 이름이 GetProductList -> DTO 이름, Service 이름, 등 비슷하거나 동일한 의미가
 * 사용되는 부분이라면 하나로 통일하고 있습니다. -> GetProductList -> 내부에서는 ReadProductList 하면 혼동이 올 수 있음.
 * <p>
 * 해당 부분 참고하셔서 통일 방안 확인 부탁드립니다.
 */
@Getter
@Data
public class GetProductListResponseDTO {

    private Meta meta;
    private List<ProductResponseDTO> productList = new ArrayList<>();

    public GetProductListResponseDTO(Page<Product> productPage) {
        this.meta = new Meta(productPage.getNumber() + 1,
            productPage.getNumberOfElements(),
            productPage.getTotalPages(),
            productPage.getTotalElements(),
            productPage.isLast());

        List<Product> productList = productPage.getContent();
        productList.forEach(product -> this.productList.add(new ProductResponseDTO(product)));
    }
}

