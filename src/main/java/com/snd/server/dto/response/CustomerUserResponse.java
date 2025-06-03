package com.snd.server.dto.response;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import com.snd.server.enums.GenderEnum;
import com.snd.server.enums.UserStatusEnum;
import com.snd.server.model.Role;
import com.snd.server.model.Session;

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
    String fullName;
    String email;
    String phone;
    String avatar;
    GenderEnum gender;
    Instant dateOfBirth;
    Integer agencyLimit;
    UserStatusEnum userStatus;
    Role role;

    @Builder.Default
    Set<Session> sessions = new HashSet<>();

}
