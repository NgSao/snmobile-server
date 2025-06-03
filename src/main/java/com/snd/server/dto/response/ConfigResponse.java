package com.snd.server.dto.response;

import java.util.HashSet;
import java.util.Set;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ConfigResponse extends BaseResponse {
    Long id;
    String storeName;
    String storeEmail;
    String storePhone;
    String storeAddress;
    String storeLogo;
    String storeDescription;
    Integer freeShippingThreshold;

    Set<ConfigDetailResponse> branches = new HashSet<>();

}
