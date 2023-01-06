package com.nono.deluxe.presentation;

import com.nono.deluxe.application.service.AuthService;
import com.nono.deluxe.application.service.ProductService;
import com.nono.deluxe.presentation.dto.MessageResponseDTO;
import com.nono.deluxe.presentation.dto.imagefile.ImageFileResponseDTO;
import com.nono.deluxe.presentation.dto.product.CreateProductRequestDto;
import com.nono.deluxe.presentation.dto.product.GetProductListResponseDTO;
import com.nono.deluxe.presentation.dto.product.GetRecordListResponseDTO;
import com.nono.deluxe.presentation.dto.product.ProductResponseDTO;
import com.nono.deluxe.presentation.dto.product.UpdateProductRequestDTO;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
@RestController
public class ProductController {

    private final ProductService productService;
    private final AuthService authService;

    /// product 생성
    @PostMapping("")
    public ResponseEntity<ProductResponseDTO> createProduct(
        @RequestHeader(value = "Authorization") String token,
        @Validated @RequestBody CreateProductRequestDto requestDto) {
        authService.validateTokenOverManagerRole(token);

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
        authService.validateTokenOverParticipantRole(token);

        GetProductListResponseDTO responseDTO =
            productService.getProductList(query, column, order, size, (page - 1), active);

        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    /// Product 상세 정보 조회.
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> getProductInfo(
        @RequestHeader(value = "Authorization") String token,
        @PathVariable(name = "productId") long productId) {
        authService.validateTokenOverParticipantRole(token);

        ProductResponseDTO responseDTO = productService.getProductInfo(productId);

        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    /// Product 상세 정보 조회.
    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<ProductResponseDTO> getProductInfoByBarcode(
        @RequestHeader(value = "Authorization") String token,
        @PathVariable(name = "barcode") String barcode) {
        authService.validateTokenOverParticipantRole(token);

        ProductResponseDTO responseDTO = productService.getProductInfoByBarcode(barcode);

        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @GetMapping("/{productId}/record")
    public ResponseEntity<GetRecordListResponseDTO> getProductRecord(
        @RequestHeader(value = "Authorization") String token,
        @PathVariable(name = "productId") long productId,
        @RequestParam(required = false, defaultValue = "0") int year,
        @RequestParam(required = false, defaultValue = "0") int month) {
        authService.validateTokenOverParticipantRole(token);

        GetRecordListResponseDTO responseDTO = productService.readProductRecord(productId, year, month);

        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
        @RequestHeader(value = "Authorization") String token,
        @PathVariable(name = "productId") long productId,
        @Validated @RequestBody UpdateProductRequestDTO requestDto) {
        authService.validateTokenOverManagerRole(token);

        ProductResponseDTO responseDTO = productService.updateProduct(productId, requestDto);

        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<MessageResponseDTO> deleteProduct(
        @RequestHeader(value = "Authorization") String token,
        @PathVariable(name = "productId") long productId) {
        authService.validateTokenOverManagerRole(token);

        MessageResponseDTO responseDTO = productService.deleteProduct(productId);

        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @PostMapping("/image")
    public ResponseEntity<ImageFileResponseDTO> saveImage(
        @RequestHeader(name = "Authorization") String token,
        @RequestPart MultipartFile imageFile) throws IOException {
        authService.validateTokenOverManagerRole(token);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(productService.saveImage(imageFile));
    }

    @GetMapping("/image/{imageId}")
    public ResponseEntity<byte[]> getImage(
        @RequestHeader(name = "Authorization") String token,
        @PathVariable(name = "imageId") long imageId,
        @RequestParam(name = "thumbnail", required = false, defaultValue = "false") boolean isThumbnail) {
        authService.validateTokenOverParticipantRole(token);

        return ResponseEntity
            .status(HttpStatus.OK)
            .header(HttpHeaders.CONTENT_TYPE, "image/png")
            .body(productService.getImage(imageId, isThumbnail));
    }
}
