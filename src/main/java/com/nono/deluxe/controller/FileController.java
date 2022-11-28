package com.nono.deluxe.controller;

import com.nono.deluxe.controller.dto.imagefile.UploadImageFileResponseDTO;
import com.nono.deluxe.service.AuthService;
import com.nono.deluxe.service.FileService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/api/v1/file")
@RestController
public class FileController {

    private final AuthService authService;
    private final FileService fileService;

    @PostMapping("/image")
    public ResponseEntity<UploadImageFileResponseDTO> uploadImageFile(
        @RequestHeader(name = "Authorization") String token,
        @RequestPart MultipartFile imageFile) throws IOException {
        authService.validateManagerToken(token);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(fileService.uploadImageFile(imageFile));
    }
}
