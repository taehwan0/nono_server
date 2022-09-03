package com.nono.deluxe.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.nono.deluxe.controller.dto.DeleteApiResponseDto;
import com.nono.deluxe.controller.dto.document.CreateDocumentRequestDto;
import com.nono.deluxe.controller.dto.document.DocumentResponseDto;
import com.nono.deluxe.controller.dto.document.UpdateDocumentRequestDto;
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
                                                 @Validated @RequestBody CreateDocumentRequestDto requestDto) {
        try {
            DecodedJWT jwt = authService.decodeToken(token);
            if(authService.isParticipant(jwt) || authService.isManager(jwt) || authService.isAdmin(jwt)) {
                long userId = authService.getUserIdByDecodedToken(jwt);
                DocumentResponseDto responseDto = documentService.createDocument(userId, requestDto);

                return ResponseEntity.status(HttpStatus.OK).body(responseDto);
            } else {
                log.error("Document: forbidden");

                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<DocumentResponseDto> readDocument(@RequestHeader(name = "Authorization") String token,
                                            @PathVariable(name = "documentId") long documentId) {
        try {
            DecodedJWT jwt = authService.decodeToken(token);
            if(authService.isParticipant(jwt) || authService.isManager(jwt) || authService.isAdmin(jwt)) {
                DocumentResponseDto responseDto = documentService.readDocument(documentId);

                return ResponseEntity.status(HttpStatus.OK).body(responseDto);
            } else {
                log.error("Document: forbidden");

                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("")
    public void readDocumentList() {

    }

    @PutMapping("/{documentId}")
    public ResponseEntity<DocumentResponseDto> updateDocument(@RequestHeader(name = "Authorization") String token,
                               @PathVariable(name = "documentId") long documentId,
                               @Validated @RequestBody UpdateDocumentRequestDto requestDto) {
        try {
            DecodedJWT jwt = authService.decodeToken(token);
            if(authService.isParticipant(jwt) || authService.isManager(jwt) || authService.isAdmin(jwt)) {
                long userId = authService.getUserIdByDecodedToken(jwt);
                log.info("Document: {} document updated By {}", documentId, userId);
                DocumentResponseDto responseDto = documentService.updateDocument(documentId, requestDto);

                return ResponseEntity.status(HttpStatus.OK).body(responseDto);
            } else {
                log.error("Document: forbidden");

                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<DeleteApiResponseDto> deleteDocument(@RequestHeader(name = "Authorization") String token,
                                                              @PathVariable(name = "documentId") long documentId) {
        try {
            DecodedJWT jwt = authService.decodeToken(token);
            if(authService.isAdmin(jwt)) {
                long userId = authService.getUserIdByDecodedToken(jwt);
                log.info("Document: {} document deleted By {}", documentId, userId);
                DeleteApiResponseDto responseDto = documentService.deleteDocument(documentId);

                return ResponseEntity.status(HttpStatus.OK).body(responseDto);
            } else {
                log.error("Document: forbidden");

                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
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
