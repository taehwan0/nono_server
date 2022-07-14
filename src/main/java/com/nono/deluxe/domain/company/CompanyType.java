package com.nono.deluxe.domain.company;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CompanyType {

    INPUT("INPUT"),
    OUTPUT("OUTPUT");

    private final String key;
}
