package com.nono.deluxe.application.service;

import com.nono.deluxe.application.client.ImageFileClient;
import com.nono.deluxe.domain.imagefile.ImageFile;
import com.nono.deluxe.domain.imagefile.ImageFileRepository;
import com.nono.deluxe.domain.product.Product;
import com.nono.deluxe.domain.product.ProductRepository;
import com.nono.deluxe.domain.record.Record;
import com.nono.deluxe.domain.record.RecordRepository;
import com.nono.deluxe.exception.NotFoundException;
import com.nono.deluxe.presentation.dto.MessageResponseDTO;
import com.nono.deluxe.presentation.dto.imagefile.ImageFileResponseDTO;
import com.nono.deluxe.presentation.dto.product.CreateProductRequestDto;
import com.nono.deluxe.presentation.dto.product.GetProductListResponseDTO;
import com.nono.deluxe.presentation.dto.product.GetRecordListResponseDTO;
import com.nono.deluxe.presentation.dto.product.ProductResponseDTO;
import com.nono.deluxe.presentation.dto.product.UpdateProductRequestDTO;
import com.nono.deluxe.utils.LocalDateCreator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ImageFileClient imageFileClient;

    private final ProductRepository productRepository;
    private final RecordRepository recordRepository;
    private final ImageFileRepository imageFileRepository;

    @Transactional
    public ProductResponseDTO createProduct(CreateProductRequestDto createProductRequestDto) {
        Product product = createProductRequestDto.toEntity();

        if (createProductRequestDto.getImageFileId() != null) {
            imageFileRepository.findById(createProductRequestDto.getImageFileId())
                .ifPresent(product::updateImageFile);
        }

        return new ProductResponseDTO(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public GetProductListResponseDTO getProductList(
        PageRequest pageRequest,
        String query,
        boolean active) {

        if (active) {
            return new GetProductListResponseDTO(productRepository.findActivePageByName(query, pageRequest));
        }
        return new GetProductListResponseDTO(productRepository.findPageByName(query, pageRequest));
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO getProductById(long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException("not exist data"));

        return new ProductResponseDTO(product);
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO getProductByBarcode(String barcode) {
        Product product = productRepository.findByBarcode(barcode)
            .orElseThrow(() -> new NotFoundException("not exist data"));

        return new ProductResponseDTO(product);
    }

    @Transactional(readOnly = true)
    public GetRecordListResponseDTO gerProductRecord(long productId, int year, int month) {
        LocalDate fromDate = LocalDateCreator.getDateOfFirstDay(year, month);
        LocalDate toDate = LocalDateCreator.getDateOfLastDay(year, month);

        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Not Exist Product."));

        List<Record> recordList = recordRepository.findAllByProductBetween(productId, fromDate, toDate);

        return new GetRecordListResponseDTO(product, recordList);
    }

    @Transactional
    public ProductResponseDTO updateProduct(long productId, UpdateProductRequestDTO updateProductRequestDTO) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException("Not Exist product."));

        product.updateInfo(updateProductRequestDTO);

        if (updateProductRequestDTO.getImageFileId() != null) {
            imageFileRepository.findById(updateProductRequestDTO.getImageFileId())
                .ifPresent(product::updateImageFile);
        }

        productRepository.save(product);

        return new ProductResponseDTO(product);
    }

    @Transactional
    public MessageResponseDTO deleteProduct(long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException("Not Found Product"));

        product.delete();
        
        return MessageResponseDTO.ofSuccess("success");
    }

    @Transactional
    public ImageFileResponseDTO saveImage(MultipartFile image) throws IOException {
        String uuid = UUID.randomUUID().toString();
        String originalPath = imageFileClient.saveOriginal(image, uuid);
        String thumbnailPath = imageFileClient.saveThumbnail(image, uuid);

        // TODO 추후에 DB에 로컬 path 저장 로직 만들기! (현재는 핫 픽스 임시 데이터..ㅠ)
        ImageFile imageFile = ImageFile.builder()
            .originalPath(originalPath)
            .thumbnailPath(thumbnailPath)
            .build();
        imageFileRepository.save(imageFile);

        String originalUrl = imageFileClient.createImageFileUrl(imageFile.getId(), false);
        String thumbnailUrl = imageFileClient.createImageFileUrl(imageFile.getId(), true);

        imageFile.updateUrl(originalUrl, thumbnailUrl);

        return new ImageFileResponseDTO(imageFile);
    }

    @Transactional
    public byte[] getImage(long imageId, boolean isThumbnail) {
        ImageFile imageFile = imageFileRepository.findById(imageId)
            .orElseThrow(() -> new NotFoundException("NotFoundImage"));

        String imageFilePath = isThumbnail
            ? imageFile.getThumbnailPath()
            : imageFile.getOriginalPath();

        File file = new File(imageFilePath);

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            return fileInputStream.readAllBytes();
        } catch (IOException e) {
            return null;
        }
    }
}
