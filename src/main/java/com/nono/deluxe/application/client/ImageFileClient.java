package com.nono.deluxe.application.client;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ImageFileClient {


    private static final String SUFFIX = ".png";
    private static final String EXTENSION = "png";
    private static final String THUMBNAIL_TAIL = "-th";

    private static final int ORIGINAL_IMAGE_WIDTH = 2000;
    private static final int ORIGINAL_IMAGE_HEIGHT = 2000;

    private static final int THUMBNAIL_IMAGE_WIDTH = 800;
    private static final int THUMBNAIL_IMAGE_HEIGHT = 800;
    private static final double THUMBNAIL_OUTPUT_QUALITY = 0.7;

    private final String localPath;

    public ImageFileClient(@Value("${file.path}") String localPath) {
        this.localPath = localPath;
    }

    public String saveOriginal(MultipartFile imageFile, String fileName) throws IOException {
        validateImageFile(imageFile);

        BufferedImage bufferedImage = ImageIO.read(imageFile.getInputStream());

        String filePath = localPath + fileName + SUFFIX;
        save(convertToOriginal(bufferedImage), filePath);

        return filePath;
    }

    public String saveThumbnail(MultipartFile imageFile, String fileName) throws IOException {
        validateImageFile(imageFile);

        BufferedImage bufferedImage = ImageIO.read(imageFile.getInputStream());

        String filePath = localPath + fileName + THUMBNAIL_TAIL + SUFFIX;
        save(convertToThumbnail(bufferedImage), filePath);

        return filePath;
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

    private void save(BufferedImage bufferedImage, String filePath) throws IOException {
        File file = new File(filePath);
        if (file.createNewFile()) {
            ImageIO.write(convertToThumbnail(bufferedImage), EXTENSION, file);
        }
    }
}
