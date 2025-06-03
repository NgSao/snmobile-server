package com.snd.server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public enum RoleEnum {

    // Quản trị hệ thống
    ROLE_ADMIN("ROLE_ADMIN"),

    ROLE_QUAN_LY_CUA_HANG("ROLE_QUAN_LY_CUA_HANG"),

    ROLE_NHAN_VIEN_BAN_HANG("ROLE_NHAN_VIEN_BAN_HANG"),

    ROLE_CHAM_SOC_KHACH_HANG("ROLE_CHAM_SOC_KHACH_HANG"),

    ROLE_NHAN_VIEN_KHO("ROLE_NHAN_VIEN_KHO"),

    // Khách hàng
    ROLE_KHACH_HANG("ROLE_KHACH_HANG");

    private final String roleName;
}
