package com.nono.deluxe.domain.S3File;


import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class S3File {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String fileName;
}
