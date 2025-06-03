package com.snd.server.dto.response;

import java.time.Instant;

import lombok.*;

import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SessionResponse {
    Long id;
    String ipAddress;
    String userAgent;
    String payload;
    String address;
    Double latitude;
    Double longitude;
    boolean flag;
    Instant lastActivity;

}
