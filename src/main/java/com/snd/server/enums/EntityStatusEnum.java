package com.snd.server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EntityStatusEnum {
    TRIAL("Thử nghiệm"),
    ACTIVE("Hoạt động"),
    HIDDEN("Bị ẩn"),
    TRASH("Thùng rác"),
    DELETED("Đã xóa");

    private final String statusName;
}
