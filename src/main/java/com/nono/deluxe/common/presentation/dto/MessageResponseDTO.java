package com.nono.deluxe.common.presentation.dto;

import lombok.Data;

@Data
public class MessageResponseDTO {

    private boolean result;
    private String message;

    public MessageResponseDTO() {
    }

    private MessageResponseDTO(boolean result, String message) {
        this.result = result;
        this.message = message;
    }

    public static MessageResponseDTO ofSuccess(String message) {
        return new MessageResponseDTO(true, message);
    }

    public static MessageResponseDTO ofFail(String message) {
        return new MessageResponseDTO(false, message);
    }
}
