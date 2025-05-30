package com.snd.server.model;

import java.time.Instant;

import com.snd.server.enums.EntityStatusEnum;
import com.snd.server.utils.DateTimeUtils;
import com.snd.server.utils.JwtUtil;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@MappedSuperclass
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class BaseEntity {
    Instant createdAt;

    Instant updatedAt;

    String createdBy;

    String updatedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    EntityStatusEnum status = EntityStatusEnum.ACTIVE;

    @PrePersist
    public void beforeCreate() {
        this.createdBy = JwtUtil.getCurrentUserLogin().isPresent() == true
                ? JwtUtil.getCurrentUserLogin().get()
                : "";
        this.createdAt = DateTimeUtils.instantNowInVietnam();
        if (this.status == null) {
            this.status = EntityStatusEnum.ACTIVE;
        }

    }

    @PreUpdate
    public void beforeUpdate() {
        this.updatedBy = JwtUtil.getCurrentUserLogin().isPresent() == true
                ? JwtUtil.getCurrentUserLogin().get()
                : "";
        this.updatedAt = DateTimeUtils.instantNowInVietnam();
    }

}
