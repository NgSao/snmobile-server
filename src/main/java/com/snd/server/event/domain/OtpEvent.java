package com.snd.server.event.domain;

import com.snd.server.event.EventType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OtpEvent {
    EventType eventType;
    String fullName;
    String email;
    String otp;

}
