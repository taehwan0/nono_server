package com.nono.deluxe.controller.dto.company;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateCompanyActiveRequestDTO {
	List<UpdateCompanyActiveDTO> companyList = new ArrayList<>();
}
