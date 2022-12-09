package com.nono.deluxe.application;

import com.amazonaws.services.s3.AmazonS3Client;
import com.nono.deluxe.domain.imagefile.ImageFile;
import com.nono.deluxe.domain.imagefile.ImageFileRepository;
import com.nono.deluxe.presentation.dto.imagefile.ImageFileResponseDTO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class FileService {

    private static final String SUFFIX = ".png";

    private static final int ORIGINAL_IMAGE_WIDTH = 2000;
    private static final int ORIGINAL_IMAGE_HEIGHT = 2000;

    private static final String THUMBNAIL_TAIL = "-th";
    private static final int THUMBNAIL_IMAGE_WIDTH = 800;
    private static final int THUMBNAIL_IMAGE_HEIGHT = 800;
    private static final double THUMBNAIL_OUTPUT_QUALITY = 0.7;

    private final ImageFileRepository imageFileRepository;
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public ImageFileResponseDTO uploadImageFile(MultipartFile imageFile) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(imageFile.getInputStream());

        String uuid = UUID.randomUUID().toString();
        String originalUrl = putS3(convertToOriginal(bufferedImage), uuid + SUFFIX);
        String thumbnailUrl = putS3(convertToThumbnail(bufferedImage), uuid + THUMBNAIL_TAIL + SUFFIX);

        ImageFile savedImageFile = ImageFile.builder()
            .originalUrl(originalUrl)
            .thumbnailUrl(thumbnailUrl)
            .build();

        imageFileRepository.save(savedImageFile);

        return new ImageFileResponseDTO(savedImageFile);
    }

    private BufferedImage convertToOriginal(BufferedImage bufferedImage) throws IOException {
        if (needResize(bufferedImage)) {
            return Thumbnails.of(bufferedImage)
                .size(ORIGINAL_IMAGE_WIDTH, ORIGINAL_IMAGE_HEIGHT)
                .asBufferedImage();
        }
        return bufferedImage;
    }

    private boolean needResize(BufferedImage bufferedImage) {
        return bufferedImage.getWidth() > ORIGINAL_IMAGE_WIDTH || bufferedImage.getHeight() > ORIGINAL_IMAGE_HEIGHT;
    }

    private BufferedImage convertToThumbnail(BufferedImage bufferedImage) throws IOException {
        return Thumbnails.of(bufferedImage)
            .size(THUMBNAIL_IMAGE_WIDTH, THUMBNAIL_IMAGE_HEIGHT)
            .outputQuality(THUMBNAIL_OUTPUT_QUALITY)
            .asBufferedImage();
    }

    private String putS3(BufferedImage bufferedImage, String fileName) throws IOException {
        File file = File.createTempFile(fileName, SUFFIX);
        ImageIO.write(bufferedImage, "png", file);

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
