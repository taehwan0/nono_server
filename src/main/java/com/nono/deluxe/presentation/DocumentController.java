package com.nono.deluxe.presentation;

import com.nono.deluxe.application.service.AuthService;
import com.nono.deluxe.application.service.DocumentService;
import com.nono.deluxe.application.service.LegacyDocumentService;
import com.nono.deluxe.presentation.dto.MessageResponseDTO;
import com.nono.deluxe.presentation.dto.document.CreateDocumentRequestDTO;
import com.nono.deluxe.presentation.dto.document.DocumentResponseDTO;
import com.nono.deluxe.presentation.dto.document.ReadDocumentListResponseDTO;
import com.nono.deluxe.presentation.dto.document.UpdateDocumentRequestDTO;
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
@RequestMapping("/api/v1/document")
@RestController
public class DocumentController {

    private final DocumentService documentService;
    private final AuthService authService;
    private final LegacyDocumentService legacyDocumentService;

    @PostMapping("")
    public ResponseEntity<Object> createDocument(
        @RequestHeader(name = "Authorization") String token,
        @Validated @RequestBody CreateDocumentRequestDTO requestDto) {

        long userId = authService.validateTokenOverParticipantRole(token);

        DocumentResponseDTO responseDto = documentService.createDocument(userId, requestDto);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<DocumentResponseDTO> readDocument(
        @RequestHeader(name = "Authorization") String token,
        @PathVariable(name = "documentId") long documentId) {
        authService.validateTokenOverParticipantRole(token);

        DocumentResponseDTO responseDto = documentService.readDocument(documentId);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("")
    public ResponseEntity<ReadDocumentListResponseDTO> readDocumentList(
        @RequestHeader(name = "Authorization") String token,
        @RequestParam(required = false, defaultValue = "") String query,
        @RequestParam(required = false, defaultValue = "date") String column,
        @RequestParam(required = false, defaultValue = "DESC") String order,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "1") int page,
        @RequestParam(required = false, defaultValue = "0") int year,
        @RequestParam(required = false, defaultValue = "0") int month) {
        authService.validateTokenOverParticipantRole(token);

        ReadDocumentListResponseDTO responseDto =
            documentService.readDocumentList(query, column, order, size, (page - 1), year, month);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/{documentId}")
    public ResponseEntity<DocumentResponseDTO> updateDocument(
        @RequestHeader(name = "Authorization") String token,
        @PathVariable(name = "documentId") long documentId,
        @Validated @RequestBody UpdateDocumentRequestDTO requestDto) {
        authService.validateTokenOverParticipantRole(token);

        DocumentResponseDTO responseDto = documentService.updateDocument(documentId, requestDto);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<MessageResponseDTO> deleteDocument(
        @RequestHeader(name = "Authorization") String token,
        @PathVariable(name = "documentId") long documentId) {
        authService.validateTokenOverAdminRole(token);

        MessageResponseDTO responseDto = documentService.deleteDocument(documentId);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    /**
     * 어떤 값들을 입력 받아서 처리할지 기준 필요. full Data, 월별 데이터, 일별 데이터, 상품별 데이터 등 document 에서는 full, 월별, 일별 데이터를 처리하게 될 수 있음.
     */
    @GetMapping("/{documentId}/xls")
    public void exportDocumentToExcel() {

    }

    @PostMapping("/legacy/trans")
    public ResponseEntity<String> transLegacyDocument() {
        legacyDocumentService.importLegacyDocument();

        return ResponseEntity.status(HttpStatus.OK).body("SUCCESS");
    }
}
