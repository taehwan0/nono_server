package com.nono.deluxe.presentation.dto.auth;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmailRequestDTO {

    @Email
    @NotBlank
    private String email;

    private String type; // JOIN, REISSUE
}
