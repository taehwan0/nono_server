package com.nono.deluxe.controller.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
public class AddUserRequestDTO {
    @NotBlank
    @Size(max = 20)
    private String userName;
}
