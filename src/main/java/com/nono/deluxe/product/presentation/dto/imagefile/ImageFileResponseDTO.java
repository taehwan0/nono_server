package com.nono.deluxe.product.presentation.dto.imagefile;

import com.nono.deluxe.product.domain.ImageFile;
import lombok.Data;

@Data
public class ImageFileResponseDTO {

    private Long imageId;
    private String originalUrl;
    private String thumbnailUrl;

    public ImageFileResponseDTO() {
    }

    public ImageFileResponseDTO(ImageFile imageFile) {
        this.imageId = imageFile.getId();
        this.originalUrl = imageFile.getOriginalUrl();
        this.thumbnailUrl = imageFile.getThumbnailUrl();
    }
}
