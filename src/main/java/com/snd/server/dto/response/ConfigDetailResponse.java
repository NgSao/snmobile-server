package com.snd.server.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ConfigDetailResponse extends BaseResponse {
    Long id;
    String storePhone;
    String storeAddress;
    String districtName;
    String wardName;
    String city;
    Integer districtId;
    String wardCode;
    Double latitude;
    Double longitude;
    Long configId;

}