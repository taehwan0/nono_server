package com.nono.deluxe.controller.dto;

import lombok.Data;

@Data
public class DeleteApiResponseDto {

    private boolean result;
    private String message;

    public DeleteApiResponseDto(boolean result, String message) {
        this.result = result;
        this.message = message;
    }
}
