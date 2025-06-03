package com.snd.server.dto.response;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import com.snd.server.enums.GenderEnum;
import com.snd.server.enums.UserStatusEnum;

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
public class CustomerUserResponse {
    String id;
    String fullName;
    String phone;
    String email;
    String profileImageUrl;

    GenderEnum gender;
    Instant birthday;
    Instant lastLoginDate;
    Instant createdAt;

    UserStatusEnum userStatus;
    String role;

    @Builder.Default
    Set<AddressResponse> addresses = new HashSet<>();

}
