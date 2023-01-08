package com.nono.deluxe.presentation;

import com.nono.deluxe.application.service.AuthService;
import com.nono.deluxe.application.service.DocumentService;
import com.nono.deluxe.application.service.LegacyDocumentService;
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
    private final AuthService authService;
    private final LegacyDocumentService legacyDocumentService;

    @PostMapping("")
    public ResponseEntity<Object> createDocument(
        @RequestHeader(name = "Authorization") String token,
        @Validated @RequestBody CreateDocumentRequestDTO createDocumentRequestDTO) {
        long userId = authService.validateTokenOverParticipantRole(token);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(documentService.createDocument(userId, createDocumentRequestDTO));
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<DocumentResponseDTO> getDocumentById(
        @RequestHeader(name = "Authorization") String token,
        @PathVariable(name = "documentId") long documentId) {
        authService.validateTokenOverParticipantRole(token);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(documentService.getDocument(documentId));
    }

    @GetMapping("")
    public ResponseEntity<ReadDocumentListResponseDTO> getDocumentList(
        @RequestHeader(name = "Authorization") String token,
        @RequestParam(required = false, defaultValue = "") String query,
        @RequestParam(required = false, defaultValue = "date") String column,
        @RequestParam(required = false, defaultValue = "DESC") String order,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "0") int year,
        @RequestParam(required = false, defaultValue = "0") int month) {
        authService.validateTokenOverParticipantRole(token);

        PageRequest pageRequest = PageRequest.of(
            page - 1,
            size,
            Sort.by(
                new Sort.Order(Sort.Direction.valueOf(order.toUpperCase()), column),
                new Sort.Order(Sort.Direction.ASC, "createdAt")));

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(documentService.getDocumentList(pageRequest, query, year, month));
    }

    @PutMapping("/{documentId}")
    public ResponseEntity<DocumentResponseDTO> updateDocument(
        @RequestHeader(name = "Authorization") String token,
        @PathVariable(name = "documentId") long documentId,
        @Validated @RequestBody UpdateDocumentRequestDTO updateDocumentRequestDTO) {
        authService.validateTokenOverParticipantRole(token);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(documentService.updateDocument(documentId, updateDocumentRequestDTO));
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<MessageResponseDTO> deleteDocument(
        @RequestHeader(name = "Authorization") String token,
        @PathVariable(name = "documentId") long documentId) {
        authService.validateTokenOverAdminRole(token);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(documentService.deleteDocument(documentId));
    }

    @GetMapping("/excel")
    public ResponseEntity<MessageResponseDTO> postMonthlyDocument(
        @RequestHeader(name = "Authorization") String token,
        @RequestParam int year,
        @RequestParam int month)
        throws IOException, MessagingException {
        long userId = authService.validateTokenOverManagerRole(token);

        documentService.postMonthDocument(userId, year, month);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(MessageResponseDTO.ofSuccess("request success"));
    }

    /**
     * TODO live 서버에서 삭제 할 것!!!
     */
    @PostMapping("/legacy/trans")
    public ResponseEntity<String> transLegacyDocument() {
        legacyDocumentService.importLegacyDocument();

        return ResponseEntity
            .status(HttpStatus.OK)
            .body("SUCCESS");
    }
}
