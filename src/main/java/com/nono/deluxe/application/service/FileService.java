package com.nono.deluxe.application.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.nono.deluxe.application.client.ExcelClient;
import com.nono.deluxe.application.client.MailClient;
import com.nono.deluxe.domain.imagefile.ImageFile;
import com.nono.deluxe.domain.imagefile.ImageFileRepository;
import com.nono.deluxe.domain.user.User;
import com.nono.deluxe.domain.user.UserRepository;
import com.nono.deluxe.presentation.dto.imagefile.ImageFileResponseDTO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@EnableAsync
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

    private final ExcelClient excelClient;
    private final MailClient mailClient;
    private final AmazonS3Client amazonS3Client;

    private final ImageFileRepository imageFileRepository;
    private final UserRepository userRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Transactional
    public ImageFileResponseDTO uploadImageFile(MultipartFile imageFile) throws IOException {
        validateImageFile(imageFile);

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

    private void validateImageFile(MultipartFile imageFile) {
        if (imageFile.isEmpty()) {
            throw new IllegalArgumentException("이미지 파일이 업로드되지 않았습니다.");
        }
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

    @Async
    @Transactional(readOnly = true)
    public void postMonthDocument(long userId, int year, int month)
        throws MessagingException, IOException {
        Optional<File> excelFile = excelClient.createMonthlyDocumentFile(year, month);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("NotFountUser"));

        String subject = subjectBuilder(year, month);

        mailClient.postExcelFile(user.getEmail(), subject, excelFile);
    }

    private String subjectBuilder(int year, int month) {
        return year + "년 " + month + "월 노노유통 월간 문서";
    }
}
