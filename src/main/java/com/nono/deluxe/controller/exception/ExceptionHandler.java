package com.nono.deluxe.controller.exception;

import com.nono.deluxe.controller.dto.MessageResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(RuntimeException.class)
    public ResponseEntity<MessageResponseDTO> test() {
        MessageResponseDTO response = new MessageResponseDTO(false, "hello");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
