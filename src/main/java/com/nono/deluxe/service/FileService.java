package com.nono.deluxe.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.nono.deluxe.controller.dto.imagefile.UploadImageFileResponseDTO;
import com.nono.deluxe.domain.imagefile.ImageFile;
import com.nono.deluxe.domain.imagefile.ImageFileRepository;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class FileService {

    private final ImageFileRepository imageFileRepository;
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public UploadImageFileResponseDTO uploadImageFile(MultipartFile imageFile) throws IOException {
        // 1. 이미지 파일을 받아서
        // 2. UUID 2개 생성
        // 2-1. 오리지널 이미지 저장 및 url 반환
        // 2-2. 섬네일 이미지 저장 및 url 반환
        // 3. entity 만들고 dto 로 반환

        String originalUrl = putS3(imageFile);
//        String thumbnailUrl = putS3(convertThumbnailImage(imageFile));

        ImageFile savedImageFile = ImageFile.builder()
            .originalUrl(originalUrl)
            .thumbnailUrl("")
            .build();

        imageFileRepository.save(savedImageFile);

        return new UploadImageFileResponseDTO(savedImageFile);
    }

    private String putS3(MultipartFile imageFile) throws IOException {
        String originalImageFileName = UUID.randomUUID() + "-" + imageFile.getOriginalFilename();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(imageFile.getInputStream().available());

        amazonS3Client.putObject(bucket, originalImageFileName, imageFile.getInputStream(), objectMetadata);
        return amazonS3Client.getUrl(bucket, originalImageFileName).toString();
    }
}
