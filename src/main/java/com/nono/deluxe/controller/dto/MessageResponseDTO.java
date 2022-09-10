package com.nono.deluxe.controller.dto;

import lombok.Data;

@Data
public class MessageResponseDTO {

    private boolean result;
    private String message;

    public MessageResponseDTO(boolean result, String message) {
        this.result = result;
        this.message = message;
    }
}
