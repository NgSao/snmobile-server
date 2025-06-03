package com.snd.server.dto.request;

import com.snd.server.enums.AddressTypeEnum;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressRequest {
    AddressTypeEnum addressType;
    String fullName;
    String phone;
    String city;
    String district;
    String street;
    String addressDetail;
    Boolean active;

}