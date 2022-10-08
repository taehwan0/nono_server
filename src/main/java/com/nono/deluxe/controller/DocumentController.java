package com.nono.deluxe.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.nono.deluxe.controller.dto.MessageResponseDTO;
import com.nono.deluxe.controller.dto.document.CreateDocumentRequestDTO;
import com.nono.deluxe.controller.dto.document.DocumentResponseDTO;
import com.nono.deluxe.controller.dto.document.ReadDocumentListResponseDTO;
import com.nono.deluxe.controller.dto.document.UpdateDocumentRequestDTO;
import com.nono.deluxe.service.AuthService;
import com.nono.deluxe.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/document")
@RestController
public class DocumentController {

    private final DocumentService documentService;
    private final AuthService authService;


    @PostMapping("")
    public ResponseEntity<Object> createDocument(@RequestHeader(name = "Authorization") String token,
                                                 @Validated @RequestBody CreateDocumentRequestDTO requestDto) {
        DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
        if (authService.isParticipant(jwt) || authService.isManager(jwt) || authService.isAdmin(jwt)) {
        }
        long userId = authService.getUserIdByDecodedToken(jwt);
        DocumentResponseDTO responseDto = documentService.createDocument(userId, requestDto);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<DocumentResponseDTO> readDocument(@RequestHeader(name = "Authorization") String token,
                                                            @PathVariable(name = "documentId") long documentId) {
        DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
        if (authService.isParticipant(jwt) || authService.isManager(jwt) || authService.isAdmin(jwt)) {
        }
        DocumentResponseDTO responseDto = documentService.readDocument(documentId);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("")
    public ResponseEntity<ReadDocumentListResponseDTO> readDocumentList(@RequestHeader(name = "Authorization") String token,
                                                                        @RequestParam(required = false, defaultValue = "") String query,
                                                                        @RequestParam(required = false, defaultValue = "date") String column,
                                                                        @RequestParam(required = false, defaultValue = "DESC") String order,
                                                                        @RequestParam(required = false, defaultValue = "10") int size,
                                                                        @RequestParam(required = false, defaultValue = "1") int page,
                                                                        @RequestParam(required = false, defaultValue = "0") int year,
                                                                        @RequestParam(required = false, defaultValue = "0") int month) {
        DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
        if (authService.isParticipant(jwt) || authService.isManager(jwt) || authService.isAdmin(jwt)) {
        }
        ReadDocumentListResponseDTO responseDto = documentService.readDocumentList(query, column, order, size, (page - 1), year, month);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/{documentId}")
    public ResponseEntity<DocumentResponseDTO> updateDocument(@RequestHeader(name = "Authorization") String token,
                                                              @PathVariable(name = "documentId") long documentId,
                                                              @Validated @RequestBody UpdateDocumentRequestDTO requestDto) {
        DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
        if (authService.isParticipant(jwt) || authService.isManager(jwt) || authService.isAdmin(jwt)) {
        }
        long userId = authService.getUserIdByDecodedToken(jwt);
        log.info("Document: {} document updated By {}", documentId, userId);
        DocumentResponseDTO responseDto = documentService.updateDocument(documentId, requestDto);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<MessageResponseDTO> deleteDocument(@RequestHeader(name = "Authorization") String token,
                                                             @PathVariable(name = "documentId") long documentId) {
        DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
        if (authService.isAdmin(jwt)) {
        }
        long userId = authService.getUserIdByDecodedToken(jwt);
        log.info("Document: {} document deleted By {}", documentId, userId);
        MessageResponseDTO responseDto = documentService.deleteDocument(documentId);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    /**
     * 어떤 값들을 입력 받아서 처리할지 기준 필요.
     * full Data, 월별 데이터, 일별 데이터, 상품별 데이터 등
     * document 에서는 full, 월별, 일별 데이터를 처리하게 될 수 있음.
     */
    @GetMapping("/{documentId}/xls")
    public void exportDocumentToExcel() {

    }
}
