package com.nono.deluxe.exception.handler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.nono.deluxe.controller.dto.ErrorResponseDTO;
import javax.validation.ConstraintViolationException;
import javax.validation.UnexpectedTypeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /*
    RuntimeException 을 상속하는 나머지 에러의 반환 형태 정의
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> runtimeException(RuntimeException exception) {
        String code = "R-Runtime";
        String message = exception.getMessage();
        ErrorResponseDTO response = new ErrorResponseDTO(HttpStatus.BAD_REQUEST, code, message);

        log.error("[RuntimeException = {}]: {}" + exception, code, message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /*
    데이터베이스에 등록할 수 없는 값이 입력됨 -> 이미 정의된 유니크한 요소가 입력됨
     */
    @ExceptionHandler({DataIntegrityViolationException.class, ConstraintViolationException.class})
    public ResponseEntity<ErrorResponseDTO> sqlException(Exception exception) {
        String code = "D-InvalidData";
        String message = exception.getMessage();
        ErrorResponseDTO response = new ErrorResponseDTO(HttpStatus.BAD_REQUEST, code, message);

        log.error("[SQLException = {}]: {}" + exception, code, message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /*
    Validation 에 문제가 있음 (값 자체에는 문제가 없을 수 있음, 형식을 제한함)
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponseDTO> validationException(Exception exception) {
        String code = "D-Validation";
        String message = exception.getMessage();
        ErrorResponseDTO response = new ErrorResponseDTO(HttpStatus.BAD_REQUEST, code, message);

        log.error("[ValidationException = {}]: {}" + exception, code, message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /*
    입력 값에 문제가 있음
     */
    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class,
        UnexpectedTypeException.class,
        InvalidFormatException.class})
    public ResponseEntity<ErrorResponseDTO> illegalArgumentException(Exception exception) {
        String code = "D-Illegal";
        String message = exception.getMessage();
        ErrorResponseDTO response = new ErrorResponseDTO(HttpStatus.BAD_REQUEST, code, message);

        log.error("[IllegalArgumentException = {}]: {}" + exception, code, message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /*
    queryParameter 의 데이터 타입이 일치하지 않음 (long 이 필요한데 문자가 입력되는 등)
     */
    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> methodArgumentTypeMismatchException(
        MethodArgumentTypeMismatchException exception) {
        String code = "D-Query";
        String message = exception.getMessage();
        ErrorResponseDTO response = new ErrorResponseDTO(HttpStatus.BAD_REQUEST, code, message);

        log.error("[MethodArgumentTypeMismatchException = {}]: {}" + exception, code, message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
