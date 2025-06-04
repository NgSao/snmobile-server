package com.snd.server.dto.request;

import com.snd.server.enums.EntityStatusEnum;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BrandRequest {
    String name;
    String imageUrl;
    Integer displayOrder;
    EntityStatusEnum status;

}
