package com.nono.deluxe.domain.document;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DocumentType {

    INPUT("INPUT"),
    OUTPUT("OUTPUT");

    private final String key;
}
