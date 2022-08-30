package com.nono.deluxe.service;


import com.nono.deluxe.controller.dto.product.*;
import com.nono.deluxe.domain.imagefile.ImageFile;
import com.nono.deluxe.domain.product.Product;
import com.nono.deluxe.domain.product.ProductRepository;
import com.nono.deluxe.domain.product.StorageType;
import com.nono.deluxe.domain.record.Record;
import com.nono.deluxe.domain.record.RecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
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

    public GetProductListResponseDTO getProductList(String query, String column, String order, int size, int page, String active) {
        List<Product> filteredProductList;
        //Step 1. 조건에 맞는 데이터 가져오기.
        if (active.toLowerCase().equals("true")) {
            filteredProductList = productRepository.findProductList(query, true, column, order);
        } else if (active.toLowerCase().equals("false")) {
            //Step 1. 조건에 맞는 데이터 가져오기.
            filteredProductList = productRepository.findProductList(query, false, column, order);
        } else {
            filteredProductList = productRepository.findProductList(query, column, order);
        }

        //Step 2.  해당 페이지에서 return 해야할 데이터 추출.
        int total = filteredProductList.size();
        int nextPage = page + 1;
        int lastIndex = total - 1;
        int startIndex = size * (page - 1);
        if (lastIndex < startIndex) {
            // 현재 원하는 부분의 데이터가 마지막 데이터 인덱스보다 뒷부분의 데이터를 요구함.
            filteredProductList = new ArrayList<>();
            nextPage = -1;
        } else {
            int tempLastIndex = startIndex + size - 1;
            if (lastIndex > tempLastIndex) {
                // 만약 return 해야 하는 데이터가 마지막 데이터 인덱스보다 적은 경우 마지막 인덱스를 변경.
                lastIndex = tempLastIndex;
                nextPage = -1;
            }
            filteredProductList = filteredProductList.subList(startIndex, lastIndex);
        }

        // step 3. 리스트 추출.
        List<ProductResponseDTO> responseDTOList = filteredProductList.stream().map(ProductResponseDTO::new).collect(Collectors.toList());
        return new GetProductListResponseDTO(total, nextPage, responseDTOList);
    }

    public ProductResponseDTO getProductInfo(long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("not exist data"));

        return new ProductResponseDTO(product);
    }

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
        ImageFile imageFile = ImageFile.builder()
                .url(requestDTO.getImage())
                .fileName(requestDTO.getName())
                .build();

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
                requestDTO.isActivate(),
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
