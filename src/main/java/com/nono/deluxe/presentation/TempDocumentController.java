package com.nono.deluxe.presentation;

import com.nono.deluxe.application.service.AuthService;
import com.nono.deluxe.application.service.TempDocumentService;
import com.nono.deluxe.presentation.dto.MessageResponseDTO;
import com.nono.deluxe.presentation.dto.tempdocument.CreateTempDocumentRequestDTO;
import com.nono.deluxe.presentation.dto.tempdocument.ReadTempDocumentListResponseDTO;
import com.nono.deluxe.presentation.dto.tempdocument.TempDocumentResponseDTO;
import com.nono.deluxe.presentation.dto.tempdocument.UpdateTempDocumentRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
        @Validated @RequestBody CreateTempDocumentRequestDTO createTempDocumentRequestDTO) {
        long userId = authService.validateTokenOverParticipantRole(token);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(tempDocumentService.createDocument(userId, createTempDocumentRequestDTO));
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<TempDocumentResponseDTO> getTempDocumentById(
        @RequestHeader(name = "Authorization") String token,
        @PathVariable(name = "documentId") long documentId) {
        authService.validateTokenOverParticipantRole(token);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(tempDocumentService.getTempDocumentById(documentId));
    }

    @GetMapping("")
    public ResponseEntity<ReadTempDocumentListResponseDTO> getTempDocumentList(
        @RequestHeader(name = "Authorization") String token,
        @RequestParam(required = false, defaultValue = "") String query,
        @RequestParam(required = false, defaultValue = "date") String column,
        @RequestParam(required = false, defaultValue = "DESC") String order,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "1") int page) {
        authService.validateTokenOverParticipantRole(token);

        PageRequest pageRequest = PageRequest.of(
            page - 1,
            size,
            Sort.by(
                new Sort.Order(Sort.Direction.valueOf(order.toUpperCase()), column),
                new Sort.Order(Sort.Direction.ASC, "createdAt")));

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(tempDocumentService.getTempDocumentList(pageRequest, query));
    }

    @PutMapping("/{documentId}")
    public ResponseEntity<TempDocumentResponseDTO> updateTempDocument(
        @RequestHeader(name = "Authorization") String token,
        @PathVariable(name = "documentId") long documentId,
        @Validated @RequestBody UpdateTempDocumentRequestDTO updateTempDocumentRequestDTO) {
        long userId = authService.validateTokenOverParticipantRole(token);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(tempDocumentService.updateDocument(documentId, userId, updateTempDocumentRequestDTO));
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<MessageResponseDTO> deleteTempDocument(
        @RequestHeader(name = "Authorization") String token,
        @PathVariable(name = "documentId") long documentId) {
        authService.validateTokenOverManagerRole(token);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(tempDocumentService.deleteDocument(documentId));
    }
}
