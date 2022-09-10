package com.nono.deluxe.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.nono.deluxe.controller.dto.product.*;
import com.nono.deluxe.service.AuthService;
import com.nono.deluxe.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class ProductController {
    ProductService productService;
    AuthService authService;

    /// product 생성
    @PostMapping("/product")
    public ResponseEntity<ProductResponseDTO> createProduct(@RequestHeader(value = "Authorization") String token,
                                                            @RequestBody CreateProductRequestDto requestDto) {
        try {
            DecodedJWT jwt = authService.decodeToken(token);
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

    /// product 리스트 가져오기.
    @GetMapping("/product/")
    public ResponseEntity<GetProductListResponseDTO> getProductList(@RequestHeader(value = "Authorization") String token,
                                                                    @RequestParam(value = "query", defaultValue = "") String query,
                                                                    @RequestParam(value = "column", defaultValue = "name") String column,
                                                                    @RequestParam(value = "order", defaultValue = "asc") String order,
                                                                    @RequestParam(value = "size", defaultValue = "10") int size,
                                                                    @RequestParam(value = "page", defaultValue = "1") int page,
                                                                    @RequestParam(value = "active", defaultValue = "all") String active) {
        try {
            DecodedJWT jwt = authService.decodeToken(token);
            if (authService.isParticipant(jwt) || authService.isManager(jwt) || authService.isAdmin(jwt)) {
                GetProductListResponseDTO responseDTO = productService.getProductList(query, column, order, size, page, active);
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(responseDTO);
            }  else {
                log.error("Product: forbidden getProductList {}", jwt.getId());
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
            DecodedJWT jwt = authService.decodeToken(token);
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

    @GetMapping("/product/{productId}/record")
    public ResponseEntity<GetRecordListResponseDTO> getProductRecord(@RequestHeader(value = "Authorization") String token,
                                                                     @PathVariable(name = "productId") long productId,
                                                                     @RequestParam(value = "year") int year,
                                                                     @RequestParam(value = "month") int month) {
        try {
            DecodedJWT jwt = authService.decodeToken(token);
            if (authService.isParticipant(jwt) || authService.isManager(jwt) || authService.isAdmin(jwt)) {
                GetRecordListResponseDTO responseDTO = productService.getProductRecordList(productId, year, month);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(responseDTO);
            } else {
                log.error("Product: forbidden getProductRecord {}", jwt.getId());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

        } catch (RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/product/{productId}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@RequestHeader(value = "Authorization") String token,
                                                            @PathVariable(name = "productId") long productId,
                                                            @RequestBody UpdateProductRequestDTO requestDto) {
        try {
            DecodedJWT jwt = authService.decodeToken(token);
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
    public ResponseEntity<ResponseDTO> deleteProduct(@RequestHeader(value = "Authorization") String token,
                                                     @PathVariable(name = "productId") long productId) {
        try {
            DecodedJWT jwt = authService.decodeToken(token);
            if (authService.isManager(jwt) || authService.isAdmin(jwt)) {
                ResponseDTO responseDTO = productService.deleteProduct(productId);
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