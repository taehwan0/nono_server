package com.nono.deluxe.controller.dto.company;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateCompanyActiveRequestDto {
    List<UpdateCompanyActiveDto> companyList = new ArrayList<>();
}