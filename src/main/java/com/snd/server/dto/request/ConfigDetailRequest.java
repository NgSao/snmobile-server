package com.snd.server.dto.request;

import com.snd.server.enums.EntityStatusEnum;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ConfigDetailRequest {
    String storePhone;
    String storeAddress;
    String districtName;
    String wardName;
    String city;
    Integer districtId;
    String wardCode;
    Double latitude;
    Double longitude;
    EntityStatusEnum status;

}
