package com.snd.server.dto.request;

import java.util.Set;

import com.snd.server.enums.RoleEnum;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminUserRequest {
    String fullName;
    String email;
    String phone;
    String password;
    RoleEnum roleEnum;
    Set<AddressRequest> address;

}
