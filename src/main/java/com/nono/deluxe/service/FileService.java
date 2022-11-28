package com.nono.deluxe.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.nono.deluxe.controller.dto.imagefile.UploadImageFileResponseDTO;
import com.nono.deluxe.domain.imagefile.ImageFile;
import com.nono.deluxe.domain.imagefile.ImageFileRepository;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class FileService {

    private static final String SUFFIX = ".png";
    private static final String THUMBNAIL_TAIL = "-th";
    private static final int IMAGE_WIDTH = 1000;
    private static final int IMAGE_HEIGHT = 1000;
    private static final double OUTPUT_QUALITY = 0.7;

    private final ImageFileRepository imageFileRepository;
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public UploadImageFileResponseDTO uploadImageFile(MultipartFile imageFile) throws IOException {
        String uuid = UUID.randomUUID().toString();
        String originalUrl = putS3(imageFile, uuid);
        String thumbnailUrl = putS3(convertToThumbnail(imageFile), uuid);

        ImageFile savedImageFile = ImageFile.builder()
            .originalUrl(originalUrl)
            .thumbnailUrl(thumbnailUrl)
            .build();

        imageFileRepository.save(savedImageFile);

        return new UploadImageFileResponseDTO(savedImageFile);
    }

    private File convertToThumbnail(MultipartFile imageFile) throws IOException {
        File file = File.createTempFile(imageFile.getName() + THUMBNAIL_TAIL, SUFFIX);
        Thumbnails.of(imageFile.getInputStream())
            .size(IMAGE_WIDTH, IMAGE_HEIGHT)
            .outputQuality(OUTPUT_QUALITY)
            .toFile(file);

        return file;
    }

    private String putS3(MultipartFile imageFile, String fileName) throws IOException {
        fileName += SUFFIX;

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(imageFile.getInputStream().available());
        objectMetadata.setContentType("image/png");

        amazonS3Client.putObject(bucket, fileName, imageFile.getInputStream(), objectMetadata);

        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private String putS3(File file, String fileName) {
        fileName += THUMBNAIL_TAIL + SUFFIX;

        amazonS3Client.putObject(bucket, fileName, file);
        deleteFile(file);

        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    private void deleteFile(File file) {
        if (!file.delete()) {
            throw new RuntimeException("썸네일 파일 제거에 실패했습니다.");
        }
    }
}
