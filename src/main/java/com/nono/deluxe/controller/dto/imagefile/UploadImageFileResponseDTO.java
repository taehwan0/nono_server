package com.nono.deluxe.controller.dto.imagefile;

import com.nono.deluxe.domain.imagefile.ImageFile;
import lombok.Data;

@Data
public class UploadImageFileResponseDTO {

    private Long fileId;
    private String originalUrl;
    private String thumbnailUrl;

    public UploadImageFileResponseDTO(ImageFile imageFile) {
        this.fileId = imageFile.getId();
        this.originalUrl = imageFile.getOriginalUrl();
        this.thumbnailUrl = imageFile.getThumbnailUrl();
    }
}
