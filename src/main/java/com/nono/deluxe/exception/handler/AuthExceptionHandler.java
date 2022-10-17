package com.nono.deluxe.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.nono.deluxe.controller.dto.ErrorResponseDTO;
import com.nono.deluxe.exception.NoAuthorityException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class AuthExceptionHandler {

	/*
	토큰의 기한이 만료됐거나, 토큰의 형식이 틀렸거나, 토큰이 변질되어 디코딩 할 수 없는 경우
	 */
	@ExceptionHandler({JWTVerificationException.class, TokenExpiredException.class, JWTCreationException.class})
	public ResponseEntity<ErrorResponseDTO> tokenException(Exception exception) {
		String code = "A-Token";
		String message;
		if (exception instanceof TokenExpiredException) {
			message = "토큰 시간이 만료되었습니다.";
		} else if (exception instanceof JWTCreationException) {
			message = "토큰 생성에 실패했습니다.";
		} else {
			message = "올바르지 않은 토큰입니다.";
		}
		ErrorResponseDTO response = new ErrorResponseDTO(HttpStatus.BAD_REQUEST, code, message);

		log.error("[AuthTokenException = {}]: {}" + exception, code, message);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	/*
	RequestHeader 의 필수 요소가 없음 -> 본 프로젝트에서는 Authorization 필수
	때문에, 401 에러를 리턴함 (인증할 것을 요구함)
	 */
	@ExceptionHandler({MissingRequestHeaderException.class})
	public ResponseEntity<ErrorResponseDTO> missingRequestHeaderException(MissingRequestHeaderException exception) {
		String header = exception.getHeaderName();

		if (!header.equals("Authorization")) {
			throw new RuntimeException("필수 헤더가 입력되지 않았습니다.");
		}

		String code = "A-MissingToken";
		String message = "요청 헤더에 토큰이 포함되지 않았습니다.";
		ErrorResponseDTO response = new ErrorResponseDTO(HttpStatus.UNAUTHORIZED, code, message);

		log.error("[MissingRequestHeaderException = {}]: {}" + exception, code, message);

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	}

	/*
	해당 API 에 접근할 권한이 없음
	 */
	@ExceptionHandler({NoAuthorityException.class})
	public ResponseEntity<ErrorResponseDTO> noAuthorityException(Exception exception) {
		String code = "A-NoAuthority";
		String message = "해당 API 에 접근하기 위한 권한이 부족합니다.";

		ErrorResponseDTO response = new ErrorResponseDTO(HttpStatus.FORBIDDEN, code, message);

		log.error("[NoAuthorityException = {}]: {}" + exception, code, message);

		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
	}
}
