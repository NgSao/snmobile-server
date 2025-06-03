package com.snd.server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatusEnum {
    UNVERIFIED("Chưa xác thực"),
    INACTIVE("Không hoạt động"),
    ACTIVE("Hoạt động"),
    BLOCKED("Đã bị khóa");

    private final String statusName;
}
