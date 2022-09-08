package com.nono.deluxe.service;


import com.nono.deluxe.controller.dto.product.*;
import com.nono.deluxe.domain.imagefile.ImageFile;
import com.nono.deluxe.domain.product.Product;
import com.nono.deluxe.domain.product.ProductRepository;
import com.nono.deluxe.domain.product.StorageType;
import com.nono.deluxe.domain.record.Record;
import com.nono.deluxe.domain.record.RecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Pageable 적용 필요
 * 연도, 월별 record 불러오기의 날짜 비교 또한 쿼리로 처리할 예정
 */
@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final RecordRepository recordRepository;

    @Transactional
    public ProductResponseDTO createProduct(CreateProductRequestDto requestDto) {
        Product product = requestDto.toEntity();
        return new ProductResponseDTO(productRepository.save(product));
    }

    /**
     * from.hj.yang
     * 해당 부분 Pageable 사용하여 변경 완료.
     */
    public GetProductListResponseDTO getProductList(String query, String column, String order, int size, int page, boolean active) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(new Sort.Order(Sort.Direction.valueOf(order.toUpperCase(Locale.ROOT)), column)));
        Page<Product> productPage;

        if (active) {
            productPage = productRepository.getActiveProductList(query, pageable);
        } else {
            productPage = productRepository.getProductList(query, pageable);
        }

        return new GetProductListResponseDTO(productPage);
    }

    public ProductResponseDTO getProductInfo(long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("not exist data"));

        return new ProductResponseDTO(product);
    }

    /**
     * from.hj.yang
     * 1. 해당 부분에 대하여 Query만으로 해당 부분의 데이터를 추출하여 처리하는 부분에 대한 공부 필요.
     */
    public GetRecordListResponseDTO getProductRecordList(long productId, int year, int month) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Not Exist Product."));

        List<Record> recordList = recordRepository.findRecordList(productId);
        LocalDateTime startDate = LocalDate.of(year, month, 1).atTime(LocalTime.MIDNIGHT);
        LocalDateTime endDate = LocalDate.of(year, month + 1, 1).atTime(LocalTime.MIDNIGHT);
        List<Record> filteredRecordList = recordList.stream()
                .filter(record -> record.getCreatedAt().compareTo(startDate) > 0 &&
                        record.getCreatedAt().compareTo(endDate) < 0)
                .collect(Collectors.toList());

        return new GetRecordListResponseDTO(product, filteredRecordList);
    }

    @Transactional
    public ProductResponseDTO updateProduct(long productId, UpdateProductRequestDTO requestDTO) {
        Product updatedProduct = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Not Exist product."));

        StorageType storageType = StorageType.valueOf(requestDTO.getStorageType());

        ImageFile imageFile = updatedProduct.getFile();
        if (imageFile != null) {
            imageFile.update(requestDTO.getImage(), requestDTO.getName());
        }

        updatedProduct.update(
                requestDTO.getProductCode(),
                requestDTO.getName(),
                requestDTO.getDescription(),
                requestDTO.getCategory(),
                requestDTO.getMaker(),
                requestDTO.getUnit(),
                storageType,
                requestDTO.getBarcode(),
                requestDTO.getStock(),
                requestDTO.isActive(),
                imageFile,
                requestDTO.getPrice(),
                requestDTO.getMargin()
        );

        productRepository.save(updatedProduct);
        return new ProductResponseDTO(updatedProduct);

    }

    @Transactional
    public ResponseDTO deleteProduct(long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product: not found id"));
        productRepository.delete(product);
        return new ResponseDTO(true, "deleted");
    }
}
