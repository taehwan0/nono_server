package com.nono.deluxe.presentation;

import com.nono.deluxe.application.AuthService;
import com.nono.deluxe.application.FileService;
import com.nono.deluxe.presentation.dto.MessageResponseDTO;
import com.nono.deluxe.presentation.dto.imagefile.ImageFileResponseDTO;
import java.io.IOException;
import javax.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<ImageFileResponseDTO> uploadImageFile(
        @RequestHeader(name = "Authorization") String token,
        @RequestPart MultipartFile imageFile) throws IOException {
        authService.validateTokenOverManagerRole(token);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(fileService.uploadImageFile(imageFile));
    }

    @GetMapping(value = "/excel")
    public ResponseEntity<MessageResponseDTO> postMonthlyDocument(
        @RequestHeader(name = "Authorization") String token,
        @RequestParam int year,
        @RequestParam int month)
        throws IOException, MessagingException {

        long userId = authService.validateTokenOverManagerRole(token);

        fileService.postMonthDocument(userId, year, month);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(new MessageResponseDTO(true, "request success"));
    }
}
