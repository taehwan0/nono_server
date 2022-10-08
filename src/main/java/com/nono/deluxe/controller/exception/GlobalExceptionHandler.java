package com.nono.deluxe.controller.exception;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.nono.deluxe.controller.dto.ErrorResponseDTO;
import com.nono.deluxe.exception.InvalidTokenException;
import com.nono.deluxe.exception.NoAuthorityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import javax.validation.UnexpectedTypeException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /*
    RuntimeException 을 상속하는 나머지 에러의 반환 형태 정의
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> runtimeException(RuntimeException exception) {
        String code = "RuntimeError";
        String message = exception.getMessage();
        ErrorResponseDTO response = new ErrorResponseDTO(code, message);

        log.error("[RuntimeException]: {}", message);
        log.error("[RuntimeException]: " + exception);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /*
    토큰의 기한이 만료됐거나, 토큰의 형식이 틀렸거나, 토큰이 변질되어 디코딩 할 수 없는 경우
     */
    @ExceptionHandler({InvalidTokenException.class, TokenExpiredException.class, JWTDecodeException.class})
    public ResponseEntity<ErrorResponseDTO> authTokenException(Exception exception) {

        String code = "";

//        if(exception instanceof InvalidTokenException || exception instanceof JWTDecodeException) {
//            code = "InvalidToken";
//        }

        if (exception instanceof TokenExpiredException) {
            code = "ExpiredToken";
        } else {
            code = "InvalidToken";
        }

        String message = exception.getMessage();
        ErrorResponseDTO response = new ErrorResponseDTO(code, message);

        log.error("[AuthTokenException]: {}", message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /*
    데이터베이스에 등록할 수 없는 값이 입력됨 -> 이미 정의된 유니크한 요소가 입력됨
     */
    @ExceptionHandler({DataIntegrityViolationException.class, ConstraintViolationException.class})
    public ResponseEntity<ErrorResponseDTO> sqlException(Exception exception) {
        String code = "ImpossibleInputData";
        String message = exception.getMessage();
        ErrorResponseDTO response = new ErrorResponseDTO(code, message);

        log.error("[SqlException]: {}", message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /*
    Validation 에 문제가 있음 (값 자체에는 문제가 없을 수 있음, 형식을 제한함)
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponseDTO> validationException(Exception exception) {
        String code = "Validation";
        String message = exception.getMessage();
        ErrorResponseDTO response = new ErrorResponseDTO(code, message);

        log.error("[ValidationException]: {}", message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /*
    입력 값에 문제가 있음
     */
    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class, UnexpectedTypeException.class,
            InvalidFormatException.class})
    public ResponseEntity<ErrorResponseDTO> illegalArgumentException(Exception exception) {
        String code = "IllegalArgument";
        String message = exception.getMessage();
        ErrorResponseDTO response = new ErrorResponseDTO(code, message);

        log.error("[IllegalArgumentException]: {}", message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /*
    RequestHeader 의 필수 요소가 없음 -> 본 프로젝트에서는 Authorization 필수
    때문에, 401 에러를 리턴함 (인증할 것을 요구함)
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> missingRequestHeaderException(MissingRequestHeaderException exception) {

        String code = "MissingRequestHeader";
        String message = exception.getMessage();
        ErrorResponseDTO response = new ErrorResponseDTO(code, message);

        log.error("[MissingRequestHeaderException]: {}", message);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /*
    해당 API 에 접근할 권한이 없음
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> noAuthorityException(NoAuthorityException exception) {

        String code = "NoAuthority";
        String message = exception.getMessage();
        ErrorResponseDTO response = new ErrorResponseDTO(code, message);

        log.error("[NoAuthorityException]: {}", message);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /*
    queryParameter 의 데이터 타입이 일치하지 않음 (long 이 필요한데 문자가 입력되는 등)
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {

        String code = "MismatchArgumentType";
        String message = exception.getMessage();
        ErrorResponseDTO response = new ErrorResponseDTO(code, message);

        log.error("[MethodArgumentTypeMismatchException]: {}", message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
