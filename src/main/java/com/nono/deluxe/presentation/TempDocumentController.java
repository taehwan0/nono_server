package com.nono.deluxe.presentation;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.nono.deluxe.application.AuthService;
import com.nono.deluxe.application.TempDocumentService;
import com.nono.deluxe.presentation.dto.MessageResponseDTO;
import com.nono.deluxe.presentation.dto.tempdocument.CreateTempDocumentRequestDTO;
import com.nono.deluxe.presentation.dto.tempdocument.ReadTempDocumentListResponseDTO;
import com.nono.deluxe.presentation.dto.tempdocument.TempDocumentResponseDTO;
import com.nono.deluxe.presentation.dto.tempdocument.UpdateTempDocumentRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/document/temp")
@RestController
public class TempDocumentController {

    private final TempDocumentService tempDocumentService;
    private final AuthService authService;

    @PostMapping("")
    public ResponseEntity<TempDocumentResponseDTO> createTempDocument(
        @RequestHeader(name = "Authorization") String token,
        @Validated @RequestBody CreateTempDocumentRequestDTO requestDto) {
        authService.validateParticipantToken(token);

        DecodedJWT decodedJWT = authService.decodeJwt(token);
        long userId = authService.getUserIdByDecodedToken(decodedJWT);

        TempDocumentResponseDTO responseDto = tempDocumentService.createDocument(userId, requestDto);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<TempDocumentResponseDTO> readTempDocument(
        @RequestHeader(name = "Authorization") String token,
        @PathVariable(name = "documentId") long documentId) {
        authService.validateParticipantToken(token);

        TempDocumentResponseDTO responseDto = tempDocumentService.readDocument(documentId);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("")
    public ResponseEntity<ReadTempDocumentListResponseDTO> readTempDocumentList(
        @RequestHeader(name = "Authorization") String token,
        @RequestParam(required = false, defaultValue = "") String query,
        @RequestParam(required = false, defaultValue = "date") String column,
        @RequestParam(required = false, defaultValue = "DESC") String order,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "0") int page) {
        authService.validateParticipantToken(token);

        ReadTempDocumentListResponseDTO responseDto =
            tempDocumentService.readDocumentList(query, column, order, size, page);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/{documentId}")
    public ResponseEntity<TempDocumentResponseDTO> updateTempDocument(
        @RequestHeader(name = "Authorization") String token,
        @PathVariable(name = "documentId") long documentId,
        @Validated @RequestBody UpdateTempDocumentRequestDTO requestDto) {
        authService.validateParticipantToken(token);

        DecodedJWT decodedJWT = authService.decodeJwt(token);
        long userId = authService.getUserIdByDecodedToken(decodedJWT);

        TempDocumentResponseDTO responseDto = tempDocumentService.updateDocument(documentId, userId, requestDto);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<MessageResponseDTO> deleteTempDocument(
        @RequestHeader(name = "Authorization") String token,
        @PathVariable(name = "documentId") long documentId) {
        authService.validateManagerToken(token);

        MessageResponseDTO responseDto = tempDocumentService.deleteDocument(documentId);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}

