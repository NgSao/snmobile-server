package com.snd.server.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthLoginResponse {
    @JsonProperty("access_token")
    private String accessToken;
    private String email;

}
