package com.nono.deluxe.domain.product;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StorageType {

    ICE("ICE"),
    COLD("COLD"),
    ROOM("ROOM");

    private final String key;
}
