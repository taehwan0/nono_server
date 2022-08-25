package com.nono.deluxe.controller.dto.company;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class DeleteCompanyResponseDto {
    private boolean result;
    private String message;

    public DeleteCompanyResponseDto(boolean result, String message) {
        this.result = result;
        this.message = message;
    }
}
