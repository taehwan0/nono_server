package com.nono.deluxe.auth.presentation.dto.auth;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmailRequestDTO {

    @Email
    @NotNull
    private String email;

    private String type; // JOIN, REISSUE
}
