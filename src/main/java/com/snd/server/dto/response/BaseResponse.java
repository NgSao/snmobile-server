package com.snd.server.dto.response;

import java.time.Instant;

import com.snd.server.enums.EntityStatusEnum;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@MappedSuperclass
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class BaseResponse {
    Instant createdAt;

    Instant updatedAt;

    String createdBy;

    String updatedBy;
    EntityStatusEnum status;
    String statusName;

}
