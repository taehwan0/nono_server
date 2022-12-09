package com.nono.deluxe.presentation.dto.imagefile;

import com.nono.deluxe.domain.imagefile.ImageFile;
import lombok.Data;

@Data
public class ImageFileResponseDTO {

    private Long fileId;
    private String originalUrl;
    private String thumbnailUrl;

    public ImageFileResponseDTO(ImageFile imageFile) {
        this.fileId = imageFile.getId();
        this.originalUrl = imageFile.getOriginalUrl();
        this.thumbnailUrl = imageFile.getThumbnailUrl();
    }
}
