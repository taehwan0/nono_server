package com.nono.deluxe.presentation;

import com.nono.deluxe.application.service.ProductService;
import com.nono.deluxe.configuration.annotation.Auth;
import com.nono.deluxe.domain.user.Role;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    @Auth(role = Role.ROLE_MANAGER)
    @PostMapping("")
    public ResponseEntity<ProductResponseDTO> createProduct(
        @Validated @RequestBody CreateProductRequestDto productRequestDto) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(productService.createProduct(productRequestDto));
    }

    @Auth
    @GetMapping("")
    public ResponseEntity<GetProductListResponseDTO> getProductList(
        @RequestParam(value = "query", defaultValue = "") String query,
        @RequestParam(value = "column", defaultValue = "name") String column,
        @RequestParam(value = "order", defaultValue = "asc") String order,
        @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "active", defaultValue = "false") boolean active) {
        PageRequest pageRequest = PageRequest.of(page - 1,
            size,
            Sort.by(new Sort.Order(Sort.Direction.valueOf(order.toUpperCase()), column)));

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(productService.getProductList(pageRequest, query, active));
    }

    @Auth
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable(name = "productId") long productId) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(productService.getProductById(productId));
    }

    @Auth
    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<ProductResponseDTO> getProductByBarcode(@PathVariable(name = "barcode") String barcode) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(productService.getProductByBarcode(barcode));
    }

    @Auth
    @GetMapping("/{productId}/record")
    public ResponseEntity<GetRecordListResponseDTO> getProductRecord(
        @PathVariable(name = "productId") long productId,
        @RequestParam(required = false, defaultValue = "0") int year,
        @RequestParam(required = false, defaultValue = "0") int month) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(productService.gerProductRecord(productId, year, month));
    }

    @Auth(role = Role.ROLE_MANAGER)
    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
        @PathVariable(name = "productId") long productId,
        @Validated @RequestBody UpdateProductRequestDTO updateProductRequestDTO) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(productService.updateProduct(productId, updateProductRequestDTO));
    }

    @Auth(role = Role.ROLE_MANAGER)
    @DeleteMapping("/{productId}")
    public ResponseEntity<MessageResponseDTO> deleteProduct(@PathVariable(name = "productId") long productId) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(productService.deleteProduct(productId));
    }

    @Auth(role = Role.ROLE_MANAGER)
    @PostMapping("/image")
    public ResponseEntity<ImageFileResponseDTO> saveImage(@RequestPart MultipartFile imageFile) throws IOException {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(productService.saveImage(imageFile));
    }

    @GetMapping("/image/{imageId}")
    public ResponseEntity<byte[]> getImage(
        @PathVariable(name = "imageId") long imageId,
        @RequestParam(name = "thumbnail", required = false, defaultValue = "false") boolean isThumbnail) {

        return ResponseEntity
            .status(HttpStatus.OK)
            .header(HttpHeaders.CONTENT_TYPE, "image/png")
            .body(productService.getImage(imageId, isThumbnail));
    }
}
