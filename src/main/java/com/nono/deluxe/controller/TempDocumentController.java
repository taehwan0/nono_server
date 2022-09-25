package com.nono.deluxe.controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.nono.deluxe.controller.dto.MessageResponseDTO;
import com.nono.deluxe.controller.dto.tempDocument.CreateTempDocumentRequestDTO;
import com.nono.deluxe.controller.dto.tempDocument.ReadTempDocumentListResponseDTO;
import com.nono.deluxe.controller.dto.tempDocument.TempDocumentResponseDTO;
import com.nono.deluxe.controller.dto.tempDocument.UpdateTempDocumentRequestDTO;
import com.nono.deluxe.service.AuthService;
import com.nono.deluxe.service.TempDocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/document/temp")
@RestController
public class TempDocumentController {

    private final TempDocumentService tempDocumentService;
    private final AuthService authService;


    @PostMapping("")
    public ResponseEntity<Object> createTempDocument(@RequestHeader(name = "Authorization") String token,
                                                 @Validated @RequestBody CreateTempDocumentRequestDTO requestDto) {
        try {
            DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
            if (authService.isParticipant(jwt) || authService.isManager(jwt) || authService.isAdmin(jwt)) {
                long userId = authService.getUserIdByDecodedToken(jwt);
                TempDocumentResponseDTO responseDto = tempDocumentService.createDocument(userId, requestDto);

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
    public ResponseEntity<TempDocumentResponseDTO> readTempDocument(@RequestHeader(name = "Authorization") String token,
                                                            @PathVariable(name = "documentId") long documentId) {
        try {
            DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
            if (authService.isParticipant(jwt) || authService.isManager(jwt) || authService.isAdmin(jwt)) {
                TempDocumentResponseDTO responseDto = tempDocumentService.readDocument(documentId);

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
    public ResponseEntity<ReadTempDocumentListResponseDTO> readTempDocumentList(@RequestHeader(name = "Authorization") String token,
                                                                            @RequestParam(required = false, defaultValue = "") String query,
                                                                            @RequestParam(required = false, defaultValue = "date") String column,
                                                                            @RequestParam(required = false, defaultValue = "DESC") String order,
                                                                            @RequestParam(required = false, defaultValue = "10") int size,
                                                                            @RequestParam(required = false, defaultValue = "0") int page) {
        try {
            DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
            if (authService.isParticipant(jwt) || authService.isManager(jwt) || authService.isAdmin(jwt)) {
                ReadTempDocumentListResponseDTO responseDto = tempDocumentService.readDocumentList(query, column, order, size, page);

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

    @PutMapping("/{documentId}")
    public ResponseEntity<TempDocumentResponseDTO> updateTempDocument(@RequestHeader(name = "Authorization") String token,
                                                              @PathVariable(name = "documentId") long documentId,
                                                              @Validated @RequestBody UpdateTempDocumentRequestDTO requestDto) {
        try {
            DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
            if (authService.isParticipant(jwt) || authService.isManager(jwt) || authService.isAdmin(jwt)) {
                long userId = authService.getUserIdByDecodedToken(jwt);
                log.info("Document: {} document updated By {}", documentId, userId);
                TempDocumentResponseDTO responseDto = tempDocumentService.updateDocument(documentId, userId, requestDto);

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
    public ResponseEntity<MessageResponseDTO> deleteTempDocument(@RequestHeader(name = "Authorization") String token,
                                                             @PathVariable(name = "documentId") long documentId) {
        try {
            DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
            if (authService.isAdmin(jwt)) {
                long userId = authService.getUserIdByDecodedToken(jwt);
                log.info("TempDocument: {} document deleted By {}", documentId, userId);
                MessageResponseDTO responseDto = tempDocumentService.deleteDocument(documentId);

                return ResponseEntity.status(HttpStatus.OK).body(responseDto);
            } else {
                log.error("TempDocument: forbidden");

                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            log.error(e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}

