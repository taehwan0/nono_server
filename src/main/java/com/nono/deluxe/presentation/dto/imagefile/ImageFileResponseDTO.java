package com.nono.deluxe.presentation.dto.imagefile;

import com.nono.deluxe.domain.imagefile.ImageFile;
import lombok.Data;

@Data
public class ImageFileResponseDTO {

    private Long imageId;

    public ImageFileResponseDTO() {
    }

    public ImageFileResponseDTO(ImageFile imageFile) {
        this.imageId = imageFile.getId();
    }
}
