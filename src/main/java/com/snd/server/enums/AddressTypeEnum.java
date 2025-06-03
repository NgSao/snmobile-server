package com.snd.server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum AddressTypeEnum {
    HOME("Nhà riêng"),
    OFFICE("Văn phòng"),
    COMPANY("Công ty"),
    OTHER("Khác");

    private final String typeName;
}
