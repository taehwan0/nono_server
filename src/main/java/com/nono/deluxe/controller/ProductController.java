package com.nono.deluxe.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.nono.deluxe.controller.dto.MessageResponseDTO;
import com.nono.deluxe.controller.dto.product.*;
import com.nono.deluxe.service.AuthService;
import com.nono.deluxe.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
/**
 * 에러 로그의 정보나 형식에 대해서는 추후 회의를 하고 규격을 정하기로..
 * 현재는 Controller 에서는 아래와 같이 if, else 로 나누어 권한에 따른 동작을 하고 catch 에서 로직의 에러를 잡으면 될 것 같습니다.
 * 403 Forbidden 의 경우 '권한이 없는 것이지 페이지가 존재한다는 것을 의미하는 보안 취약점이 될 수 있다.' 라고 들은적이 없어 사용을 지양하려 했는데,
 * 프론트엔드에서 권한에 따라 UI 가 visible 이 변경되는 방식으로 1차 방지를 하고 있고, 이에따라 403 을 클라이언트가 받게 될 상황이 없지 않을까 생각하여,
 * 저의 코드에서는 403 을 반영을 했습니다. 상태 코드가 주어지는 편이 프론트에서 대응하기가 좋지 않을까 판단했습니다.
 */

/**
 from.hj.yang
 보안적인 부분과 프론트엔드단에서 활용하고자 하는 부분의 중간점을 찾는 부분은 쉬운 부분은 아닙니다.
 다만 왜 오류코드로 내리는 부분이 보안 취약점이 되는지에 대한 이해와 그에 대한 올바른 우회방법을 고민해보고 난 이후 결정하는 것이 옳아 보입니다.
 https://wook-2124.tistory.com/355 참고하면 좋을 것 같습니다.
 해당 부분을 검토해 보시고 좋은 방향을 결정 부탁드립니다.
 */
@Slf4j
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class ProductController {

    private final ProductService productService;
    private final AuthService authService;

    /// product 생성
    @PostMapping("/product")
    public ResponseEntity<ProductResponseDTO> createProduct(@RequestHeader(value = "Authorization") String token,
                                                            @RequestBody CreateProductRequestDto requestDto) {
        try {
            DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
            if (authService.isManager(jwt) || authService.isAdmin(jwt)) {
                ProductResponseDTO responseDTO = productService.createProduct(requestDto);
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(responseDTO);
            } else {
                log.error("This user is not authorized this api.: createProduct");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } catch (RuntimeException exception) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }


    /**
     * GetProductList
     * @param token : access Token
     * @param query : productName Filter Query
     * @param column : filter category
     * @param order : order by
     * @param size : how many data in one page
     * @param page : Which page want.
     * @param active : Need activate data?
     * @return Product List
     *
     * from.hj.yang
     * java에서 구현해주는 javadoc 문서양식이라 한번 서봤슴당.ㅎ
     * active 사용은 가능하면 boolean이 좋다고 생각합니다.
     * 다만 inactive / active / all 3가지를 모두 지원해야 한다고 한다면
     * 해당 부분을 activateType 으로 해서 String 형태가 더 맞지 않나 하는 생각도 듭니다.
     * 그렇지 않으면 active = true일때 만 active한거 내려주고 아니면 전체 다 내려준다 2가지로만 봐도 될듯합니다.
     * 혹은 isHideInavtive data 같은 느낌?ㅎ
     * 결정 검토 부탁드립니다.
     */
    /// product 리스트 가져오기.
    @GetMapping("/product") // -> path 수정, active 사용 방법 & page 의 시작은 0? 1? 결정필요
    public ResponseEntity<GetProductListResponseDTO> getProductList(@RequestHeader(value = "Authorization") String token,
                                                                    @RequestParam(value = "query", defaultValue = "") String query,
                                                                    @RequestParam(value = "column", defaultValue = "name") String column,
                                                                    @RequestParam(value = "order", defaultValue = "asc") String order,
                                                                    @RequestParam(value = "size", defaultValue = "10") int size,
                                                                    @RequestParam(value = "page", defaultValue = "1") int page,
                                                                    @RequestParam(value = "active", defaultValue = "false") boolean active) {
        try {
            DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
            if (authService.isParticipant(jwt) || authService.isManager(jwt) || authService.isAdmin(jwt)) {
                GetProductListResponseDTO responseDTO = productService.getProductList(query, column, order, size, (page -1), active);
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(responseDTO);
            }  else {
                log.error("Product: forbidden getProductList {}", jwt.getClaim("userId"));
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (RuntimeException exception) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        }
    }

    /// Product 상세 정보 조회.
    @GetMapping("/product/{productId}")
    public ResponseEntity<ProductResponseDTO> getProductInfo(@RequestHeader(value = "Authorization") String token,
                                                             @PathVariable(name = "productId") long productId) {
        try {
            DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
            if (authService.isParticipant(jwt) || authService.isManager(jwt) || authService.isAdmin(jwt)) {
                ProductResponseDTO responseDTO = productService.getProductInfo(productId);
                return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
            } else {
                log.error("Product: forbidden getProductInfo {}", jwt.getId());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /// Product 상세 정보 조회.
    @GetMapping("/product/barcode/{barcode}")
    public ResponseEntity<ProductResponseDTO> getProductInfoByBarcode(@RequestHeader(value = "Authorization") String token,
                                                             @PathVariable(name = "barcode") String barcode) {
        try {
            DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
            if (authService.isParticipant(jwt) || authService.isManager(jwt) || authService.isAdmin(jwt)) {
                ProductResponseDTO responseDTO = productService.getProductInfoByBarcode(barcode);
                return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
            } else {
                log.error("Product: forbidden getProductInfo {}", jwt.getId());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/product/{productId}/record")
    public ResponseEntity<GetRecordListResponseDTO> getProductRecord(@RequestHeader(value = "Authorization") String token,
                                                                     @PathVariable(name = "productId") long productId,
                                                                     @RequestParam(required = false, defaultValue = "0") int year,
                                                                     @RequestParam(required = false, defaultValue = "0") int month) {
        try {
            DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
            if (authService.isParticipant(jwt) || authService.isManager(jwt) || authService.isAdmin(jwt)) {
                GetRecordListResponseDTO responseDTO = productService.readProductRecord(productId, year, month);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(responseDTO);
            } else {
                log.error("Product: forbidden getProductRecord {}", jwt.getId());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

        } catch (RuntimeException exception) {
            exception.printStackTrace();
            log.error(exception.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/product/{productId}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@RequestHeader(value = "Authorization") String token,
                                                            @PathVariable(name = "productId") long productId,
                                                            @RequestBody UpdateProductRequestDTO requestDto) {
        try {
            DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
            if (authService.isManager(jwt) || authService.isAdmin(jwt)) {
                ProductResponseDTO responseDTO = productService.updateProduct(productId, requestDto);
                return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
            } else {
                log.error("This user is not authorized this api.: updateProduct");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } catch (RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<MessageResponseDTO> deleteProduct(@RequestHeader(value = "Authorization") String token,
                                                            @PathVariable(name = "productId") long productId) {
        try {
            DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
            if (authService.isManager(jwt) || authService.isAdmin(jwt)) {
                MessageResponseDTO responseDTO = productService.deleteProduct(productId);
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(responseDTO);
            } else {
                log.error("This user is not authorized this api.: updateProduct");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } catch (RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}