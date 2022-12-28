package com.nono.deluxe.application;

import com.amazonaws.services.kms.model.NotFoundException;
import com.nono.deluxe.domain.imagefile.ImageFileRepository;
import com.nono.deluxe.domain.product.Product;
import com.nono.deluxe.domain.product.ProductRepository;
import com.nono.deluxe.domain.record.Record;
import com.nono.deluxe.domain.record.RecordRepository;
import com.nono.deluxe.presentation.dto.MessageResponseDTO;
import com.nono.deluxe.presentation.dto.product.CreateProductRequestDto;
import com.nono.deluxe.presentation.dto.product.GetProductListResponseDTO;
import com.nono.deluxe.presentation.dto.product.GetRecordListResponseDTO;
import com.nono.deluxe.presentation.dto.product.ProductResponseDTO;
import com.nono.deluxe.presentation.dto.product.UpdateProductRequestDTO;
import com.nono.deluxe.utils.LocalDateCreator;
import java.time.LocalDate;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
            imageFileRepository.findById(requestDto.getImageFileId())
                .ifPresent(product::updateImageFile);
        }

        return new ProductResponseDTO(productRepository.save(product));
    }

    public GetProductListResponseDTO getProductList(
        String query,
        String column,
        String order,
        int size,
        int page,
        boolean active) {
        Pageable pageable =
            PageRequest.of(page, size, Sort.by(new Sort.Order(Sort.Direction.valueOf(order.toUpperCase()), column)));

        if (active) {
            return new GetProductListResponseDTO(productRepository.readActiveProductList(query, pageable));
        }
        return new GetProductListResponseDTO(productRepository.readProductList(query, pageable));
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

    public GetRecordListResponseDTO readProductRecord(long productId, int year, int month) {
        LocalDate fromDate = LocalDateCreator.getDateOfFirstDay(year, month);
        LocalDate toDate = LocalDateCreator.getDateOfLastDay(year, month);

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
            imageFileRepository.findById(requestDTO.getImageFileId())
                .ifPresent(updatedProduct::updateImageFile);
        }

        productRepository.save(updatedProduct);

        return new ProductResponseDTO(updatedProduct);
    }

    @Transactional
    public MessageResponseDTO deleteProduct(long productId) {
        productRepository.findById(productId)
            .ifPresent(Product::delete);

        return new MessageResponseDTO(true, "deleted");
    }
}
