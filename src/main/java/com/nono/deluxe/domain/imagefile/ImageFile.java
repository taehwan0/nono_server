package com.nono.deluxe.domain.imagefile;

import javax.persistence.Column;
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

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String fileName;

    @Builder
    public ImageFile(String url, String fileName) {
        this.url = url;
        this.fileName = fileName;
    }

    public void update(String url, String fileName) {
        this.url = url;
        this.fileName = fileName;
    }
}
