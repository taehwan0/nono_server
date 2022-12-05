package com.nono.deluxe.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.nono.deluxe.controller.dto.MessageResponseDTO;
import com.nono.deluxe.controller.dto.product.CreateProductRequestDto;
import com.nono.deluxe.controller.dto.product.GetProductListResponseDTO;
import com.nono.deluxe.controller.dto.product.GetRecordListResponseDTO;
import com.nono.deluxe.controller.dto.product.ProductResponseDTO;
import com.nono.deluxe.controller.dto.product.UpdateProductRequestDTO;
import com.nono.deluxe.domain.imagefile.ImageFile;
import com.nono.deluxe.domain.imagefile.ImageFileRepository;
import com.nono.deluxe.domain.product.Product;
import com.nono.deluxe.domain.product.ProductRepository;
import com.nono.deluxe.domain.record.Record;
import com.nono.deluxe.domain.record.RecordRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * Pageable 적용 필요 연도, 월별 record 불러오기의 날짜 비교 또한 쿼리로 처리할 예정
 */
@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final RecordRepository recordRepository;
    private final ImageFileRepository imageFileRepository;

    @Transactional
    public ProductResponseDTO createProduct(CreateProductRequestDto requestDto) {
        Product product = requestDto.toEntity();
        if (requestDto.getImageFileId() != null) {
            ImageFile imageFile = imageFileRepository.findById(requestDto.getImageFileId())
                .orElseThrow(() -> new NotFoundException("Not exist ImageFile"));

            product.updateImageFile(imageFile);
        }
        
        return new ProductResponseDTO(productRepository.save(product));
    }

    /**
     * from.hj.yang 해당 부분 Pageable 사용하여 변경 완료.
     */
    public GetProductListResponseDTO getProductList(String query, String column, String order, int size, int page,
        boolean active) {
        Pageable pageable = PageRequest.of(page, size,
            Sort.by(new Sort.Order(Sort.Direction.valueOf(order.toUpperCase(Locale.ROOT)), column)));
        Page<Product> productPage;

        if (active) {
            productPage = productRepository.readActiveProductList(query, pageable); // true -> active 만 읽기
        } else {
            productPage = productRepository.readProductList(query, pageable); // false -> 전체 읽기
        }

        return new GetProductListResponseDTO(productPage);
    }

    public ProductResponseDTO getProductInfo(long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException("not exist data"));

        return new ProductResponseDTO(product);
    }

    public ProductResponseDTO getProductInfoByBarcode(String barcode) {
        Product product = productRepository.findByBarcode(barcode)
            .orElseThrow(() -> new NotFoundException("not exist data"));

        return new ProductResponseDTO(product);
    }

    /**
     * from.hj.yang 1. 해당 부분에 대하여 Query만으로 해당 부분의 데이터를 추출하여 처리하는 부분에 대한 공부 필요.
     */
    public GetRecordListResponseDTO readProductRecord(long productId, int year, int month) {
        if (year == 0) {
            year = LocalDate.now().getYear();
        }
        int toMonth = month;
        if (month == 0) {
            month = 1;
            toMonth = 12;
        }

        LocalDate fromDate = LocalDate.of(year, month, 1);
        LocalDate toDate = LocalDate.of(year, toMonth, LocalDate.of(year, toMonth, 1).lengthOfMonth());

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Not Exist Product."));
        List<Record> recordList = recordRepository.findByProductId(productId, fromDate, toDate);

        return new GetRecordListResponseDTO(product, recordList);
    }

    @Transactional
    public ProductResponseDTO updateProduct(long productId, UpdateProductRequestDTO requestDTO) {
        Product updatedProduct = productRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException("Not Exist product."));

        updatedProduct.updateInfo(requestDTO);

        if (requestDTO.getImageFileId() != null) {
            ImageFile newImageFile = imageFileRepository.findById(requestDTO.getImageFileId())
                .orElseThrow(() -> new NotFoundException("Not Exist Image"));
            updatedProduct.updateImageFile(newImageFile);
        }

        productRepository.save(updatedProduct);

        return new ProductResponseDTO(updatedProduct);
    }

    @Transactional
    public MessageResponseDTO deleteProduct(long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException("Product: not found id"));
        product.delete();

        return new MessageResponseDTO(true, "deleted");
    }
}
