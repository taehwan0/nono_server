package com.nono.deluxe.presentation;

import com.nono.deluxe.application.AuthService;
import com.nono.deluxe.application.ExcelService;
import com.nono.deluxe.application.FileService;
import com.nono.deluxe.presentation.dto.imagefile.ImageFileResponseDTO;
import java.io.File;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    private final ExcelService excelService;

    @PostMapping("/image")
    public ResponseEntity<ImageFileResponseDTO> uploadImageFile(
        @RequestHeader(name = "Authorization") String token,
        @RequestPart MultipartFile imageFile) throws IOException {
        authService.validateTokenOverManagerRole(token);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(fileService.uploadImageFile(imageFile));
    }

    @GetMapping(value = "/excel")
    public ResponseEntity<byte[]> test() throws IOException {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(
            MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        );

        File file = excelService.getProductsRecord(2022, List.of(1, 2, 3));
        byte[] bytes = FileUtils.readFileToByteArray(file);

        return ResponseEntity
            .ok()
            .headers(headers)
            .body(bytes);
    }
}
