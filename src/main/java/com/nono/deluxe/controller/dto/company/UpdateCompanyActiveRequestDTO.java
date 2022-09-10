package com.nono.deluxe.controller.dto.company;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateCompanyActiveRequestDTO {
    List<UpdateCompanyActiveDTO> companyList = new ArrayList<>();
}