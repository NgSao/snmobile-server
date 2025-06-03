package com.snd.server.dto.request;

import com.snd.server.enums.UserStatusEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusChangeRequest {

    private String id;

    private UserStatusEnum userStatusEnum;
}
