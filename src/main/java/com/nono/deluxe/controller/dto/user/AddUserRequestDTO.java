package com.nono.deluxe.controller.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class AddUserRequestDTO {
    @NotBlank
    private String userName;
}
