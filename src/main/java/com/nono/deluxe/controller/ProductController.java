package com.nono.deluxe.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.nono.deluxe.controller.dto.MessageResponseDTO;
import com.nono.deluxe.controller.dto.product.CreateProductRequestDto;
import com.nono.deluxe.controller.dto.product.GetProductListResponseDTO;
import com.nono.deluxe.controller.dto.product.GetRecordListResponseDTO;
import com.nono.deluxe.controller.dto.product.ProductResponseDTO;
import com.nono.deluxe.controller.dto.product.UpdateProductRequestDTO;
import com.nono.deluxe.service.AuthService;
import com.nono.deluxe.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
@RestController
public class ProductController {

    private final ProductService productService;
    private final AuthService authService;

    /// product 생성
    @PostMapping("")
    public ResponseEntity<ProductResponseDTO> createProduct(@RequestHeader(value = "Authorization") String token,
        @Validated @RequestBody CreateProductRequestDto requestDto) {
        DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
        authService.verifyManagerRole(jwt);
        ProductResponseDTO responseDTO = productService.createProduct(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    /**
     * GetProductList
     *
     * @param token  : access Token
     * @param query  : productName Filter Query
     * @param column : filter category
     * @param order  : order by
     * @param size   : how many data in one page
     * @param page   : Which page want.
     * @param active : Need activate data?
     * @return Product List
     */

    // product 리스트 가져오기.
    @GetMapping("")
    public ResponseEntity<GetProductListResponseDTO> getProductList(
        @RequestHeader(value = "Authorization") String token,
        @RequestParam(value = "query", defaultValue = "") String query,
        @RequestParam(value = "column", defaultValue = "name") String column,
        @RequestParam(value = "order", defaultValue = "asc") String order,
        @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "active", defaultValue = "false") boolean active) {
        DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
        authService.verifyParticipantRole(jwt);
        GetProductListResponseDTO responseDTO = productService.getProductList(query, column, order, size, (page - 1),
            active);
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    /// Product 상세 정보 조회.
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> getProductInfo(@RequestHeader(value = "Authorization") String token,
        @PathVariable(name = "productId") long productId) {
        DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
        authService.verifyParticipantRole(jwt);
        ProductResponseDTO responseDTO = productService.getProductInfo(productId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    /// Product 상세 정보 조회.
    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<ProductResponseDTO> getProductInfoByBarcode(
        @RequestHeader(value = "Authorization") String token,
        @PathVariable(name = "barcode") String barcode) {
        DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
        authService.verifyParticipantRole(jwt);
        ProductResponseDTO responseDTO = productService.getProductInfoByBarcode(barcode);
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @GetMapping("/{productId}/record")
    public ResponseEntity<GetRecordListResponseDTO> getProductRecord(
        @RequestHeader(value = "Authorization") String token,
        @PathVariable(name = "productId") long productId,
        @RequestParam(required = false, defaultValue = "0") int year,
        @RequestParam(required = false, defaultValue = "0") int month) {
        DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
        authService.verifyParticipantRole(jwt);
        GetRecordListResponseDTO responseDTO = productService.readProductRecord(productId, year, month);
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@RequestHeader(value = "Authorization") String token,
        @PathVariable(name = "productId") long productId,
        @Validated @RequestBody UpdateProductRequestDTO requestDto) {
        DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
        authService.verifyManagerRole(jwt);
        ProductResponseDTO responseDTO = productService.updateProduct(productId, requestDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<MessageResponseDTO> deleteProduct(@RequestHeader(value = "Authorization") String token,
        @PathVariable(name = "productId") long productId) {
        DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
        authService.verifyManagerRole(jwt);
        MessageResponseDTO responseDTO = productService.deleteProduct(productId);
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(responseDTO);
    }
}
