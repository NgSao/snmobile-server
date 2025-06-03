package com.snd.server.dto.request;

import java.util.HashSet;
import java.util.Set;

import com.snd.server.enums.EntityStatusEnum;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ConfigRequest {
    String storeName;
    String storeEmail;
    String storePhone;
    String storeAddress;
    String storeLogo;
    String storeDescription;
    Integer freeShippingThreshold;
    Set<ConfigDetailRequest> branches = new HashSet<>();
    EntityStatusEnum status;

}
