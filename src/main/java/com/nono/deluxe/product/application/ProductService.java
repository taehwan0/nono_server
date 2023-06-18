package com.nono.deluxe.product.application;

import com.nono.deluxe.common.exception.NotFoundException;
import com.nono.deluxe.common.presentation.dto.MessageResponseDTO;
import com.nono.deluxe.common.utils.LocalDateCreator;
import com.nono.deluxe.document.domain.DocumentType;
import com.nono.deluxe.document.domain.Record;
import com.nono.deluxe.document.domain.repository.RecordRepository;
import com.nono.deluxe.product.domain.ImageFile;
import com.nono.deluxe.product.domain.Product;
import com.nono.deluxe.product.domain.repository.ImageFileRepository;
import com.nono.deluxe.product.domain.repository.ProductRepository;
import com.nono.deluxe.product.presentation.dto.imagefile.ImageFileResponseDTO;
import com.nono.deluxe.product.presentation.dto.product.CreateProductRequestDto;
import com.nono.deluxe.product.presentation.dto.product.GetProductListResponseDTO;
import com.nono.deluxe.product.presentation.dto.product.GetRecordListResponseDTO;
import com.nono.deluxe.product.presentation.dto.product.ProductResponseDTO;
import com.nono.deluxe.product.presentation.dto.product.UpdateProductRequestDTO;
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

    /**
     * product에 해당하는 record의 stock을 다시 계산함
     *
     * @param productId
     * @param fromDate
     * @param toDate
     * @return
     */
    @Transactional
    public boolean summationRecordFrom(long productId, LocalDate fromDate, LocalDate toDate) {
        productRepository.findById(productId)
            .orElseThrow(() -> new NotFoundException("NotFountProduct"));

        List<Record> records = recordRepository
            .getRecordsByProductIdBetweenDates(fromDate, toDate, productId);

        Long nextStock = null;
        for (Record record : records) {
            if (nextStock == null) {
                nextStock = record.getStock();
            } else {
                DocumentType type = record.getDocument().getType();
                long quantity = type == DocumentType.INPUT ? record.getQuantity() : record.getQuantity() * -1;
                nextStock += quantity;
                record.updateStock(nextStock);
            }
        }
        return true;
    }
}
