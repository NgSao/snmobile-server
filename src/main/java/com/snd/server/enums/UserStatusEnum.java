package com.snd.server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum UserStatusEnum {
    UNVERIFIED("Chưa xác thực"),
    INACTIVE("Không hoạt động"),
    ACTIVE("Hoạt động"),
    BLOCKED("Đã bị khóa");

    private final String statusName;
}
