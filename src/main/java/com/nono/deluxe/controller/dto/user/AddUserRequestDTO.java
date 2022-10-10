package com.nono.deluxe.controller.dto.user;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
public class AddUserRequestDTO {
    @NotNull
    @Size(max = 20)
    private String userName;
}
