package com.nono.deluxe.product.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class ImageFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String originalUrl;

    private String thumbnailUrl;

    private String originalPath;

    private String thumbnailPath;

    @Builder
    public ImageFile(String originalUrl, String thumbnailUrl, String originalPath, String thumbnailPath) {
        this.originalUrl = originalUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.originalPath = originalPath;
        this.thumbnailPath = thumbnailPath;
    }

    public void updatePath(String originalPath, String thumbnailPath) {
        this.originalPath = originalPath;
        this.thumbnailPath = thumbnailPath;
    }

    public void updateUrl(String originalUrl, String thumbnailUrl) {
        this.originalUrl = originalUrl;
        this.thumbnailUrl = thumbnailUrl;
    }
}
