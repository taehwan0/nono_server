package com.nono.deluxe.presentation;

import com.nono.deluxe.application.client.TokenClient;
import com.nono.deluxe.application.service.DocumentService;
import com.nono.deluxe.configuration.annotation.Auth;
import com.nono.deluxe.domain.user.Role;
import com.nono.deluxe.presentation.dto.MessageResponseDTO;
import com.nono.deluxe.presentation.dto.document.CreateDocumentRequestDTO;
import com.nono.deluxe.presentation.dto.document.DocumentResponseDTO;
import com.nono.deluxe.presentation.dto.document.ReadDocumentListResponseDTO;
import com.nono.deluxe.presentation.dto.document.UpdateDocumentRequestDTO;
import java.io.IOException;
import javax.mail.MessagingException;
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
@RequestMapping("/api/v1/document")
@RestController
public class DocumentController {

    private final DocumentService documentService;
    private final TokenClient tokenClient;

    @Auth
    @PostMapping("")
    public ResponseEntity<DocumentResponseDTO> createDocument(
        @RequestHeader(name = "Authorization") String token,
        @Validated @RequestBody CreateDocumentRequestDTO createDocumentRequestDTO) {
        long userId = tokenClient.getUserIdByToken(token);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(documentService.createDocument(userId, createDocumentRequestDTO));
    }

    @Auth
    @GetMapping("/{documentId}")
    public ResponseEntity<DocumentResponseDTO> getDocumentById(@PathVariable(name = "documentId") long documentId) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(documentService.getDocument(documentId));
    }

    @Auth
    @GetMapping("")
    public ResponseEntity<ReadDocumentListResponseDTO> getDocumentList(
        @RequestParam(required = false, defaultValue = "") String query,
        @RequestParam(required = false, defaultValue = "date") String column,
        @RequestParam(required = false, defaultValue = "DESC") String order,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "0") int year,
        @RequestParam(required = false, defaultValue = "0") int month,
        @RequestParam(required = false, defaultValue = "true") boolean record) {
        PageRequest pageRequest = PageRequest.of(
            page - 1,
            size,
            Sort.by(
                new Sort.Order(Sort.Direction.valueOf(order.toUpperCase()), column),
                new Sort.Order(Sort.Direction.ASC, "createdAt")));

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(documentService.getDocumentList(pageRequest, query, year, month, record));
    }

    @Auth
    @PutMapping("/{documentId}")
    public ResponseEntity<DocumentResponseDTO> updateDocument(
        @PathVariable(name = "documentId") long documentId,
        @Validated @RequestBody UpdateDocumentRequestDTO updateDocumentRequestDTO) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(documentService.updateDocument(documentId, updateDocumentRequestDTO));
    }

    @Auth
    @DeleteMapping("/{documentId}")
    public ResponseEntity<MessageResponseDTO> deleteDocument(@PathVariable(name = "documentId") long documentId) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(documentService.deleteDocument(documentId));
    }

    @Auth(role = Role.ROLE_MANAGER)
    @GetMapping("/excel")
    public ResponseEntity<MessageResponseDTO> postMonthlyDocument(
        @RequestHeader(name = "Authorization") String token,
        @RequestParam int year,
        @RequestParam int month)
        throws IOException, MessagingException {
        long userId = tokenClient.getUserIdByToken(token);

        documentService.postMonthDocument(userId, year, month);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(MessageResponseDTO.ofSuccess("request success"));
    }
}
