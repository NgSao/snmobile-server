package com.snd.server.dto.request;

import com.snd.server.enums.RoleEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleChangeRequest {

    private String id;

    private RoleEnum roleEnum;
}
