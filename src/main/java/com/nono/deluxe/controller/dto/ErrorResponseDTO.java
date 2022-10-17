package com.nono.deluxe.controller.dto;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDTO {
	private HttpStatus http_status;
	private String error_code;
	private String message;

	public ErrorResponseDTO(String errorCode, String message) {
		this.error_code = errorCode;
		this.message = message;
	}
}
