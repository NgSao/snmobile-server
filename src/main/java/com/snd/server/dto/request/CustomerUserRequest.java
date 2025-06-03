package com.snd.server.dto.request;

import java.time.Instant;

import com.snd.server.enums.GenderEnum;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerUserRequest {
    String fullName;
    String phone;
    String profileImageUrl;
    Instant birthday;
    GenderEnum gender;
}