package com.nono.deluxe.controller;

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

import com.auth0.jwt.interfaces.DecodedJWT;
import com.nono.deluxe.controller.dto.MessageResponseDTO;
import com.nono.deluxe.controller.dto.tempdocument.CreateTempDocumentRequestDTO;
import com.nono.deluxe.controller.dto.tempdocument.ReadTempDocumentListResponseDTO;
import com.nono.deluxe.controller.dto.tempdocument.TempDocumentResponseDTO;
import com.nono.deluxe.controller.dto.tempdocument.UpdateTempDocumentRequestDTO;
import com.nono.deluxe.service.AuthService;
import com.nono.deluxe.service.TempDocumentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
		DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
		authService.verifyParticipantRole(jwt);
		long userId = authService.getUserIdByDecodedToken(jwt);
		TempDocumentResponseDTO responseDto = tempDocumentService.createDocument(userId, requestDto);

		return ResponseEntity.status(HttpStatus.OK).body(responseDto);
	}

	@GetMapping("/{documentId}")
	public ResponseEntity<TempDocumentResponseDTO> readTempDocument(@RequestHeader(name = "Authorization") String token,
			@PathVariable(name = "documentId") long documentId) {
		DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
		authService.verifyParticipantRole(jwt);
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
		DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
		authService.verifyParticipantRole(jwt);
		ReadTempDocumentListResponseDTO responseDto = tempDocumentService.readDocumentList(query, column, order, size,
				page);

		return ResponseEntity.status(HttpStatus.OK).body(responseDto);
	}

	@PutMapping("/{documentId}")
	public ResponseEntity<TempDocumentResponseDTO> updateTempDocument(
			@RequestHeader(name = "Authorization") String token,
			@PathVariable(name = "documentId") long documentId,
			@Validated @RequestBody UpdateTempDocumentRequestDTO requestDto) {
		DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
		authService.verifyParticipantRole(jwt);
		long userId = authService.getUserIdByDecodedToken(jwt);
		log.info("Document: {} document updated By {}", documentId, userId);
		TempDocumentResponseDTO responseDto = tempDocumentService.updateDocument(documentId, userId, requestDto);

		return ResponseEntity.status(HttpStatus.OK).body(responseDto);
	}

	@DeleteMapping("/{documentId}")
	public ResponseEntity<MessageResponseDTO> deleteTempDocument(@RequestHeader(name = "Authorization") String token,
			@PathVariable(name = "documentId") long documentId) {
		DecodedJWT jwt = authService.decodeAccessTokenByRequestHeader(token);
		authService.verifyManagerRole(jwt);
		long userId = authService.getUserIdByDecodedToken(jwt);
		log.info("TempDocument: {} document deleted By {}", documentId, userId);
		MessageResponseDTO responseDto = tempDocumentService.deleteDocument(documentId);

		return ResponseEntity.status(HttpStatus.OK).body(responseDto);
	}
}

