package com.nono.deluxe.controller.dto.auth;

import com.nono.deluxe.domain.checkemail.CheckType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;

@Getter
@NoArgsConstructor
public class EmailRequestDTO {
    @Email
    private String email;

    private String type; // JOIN, REISSUE
}
