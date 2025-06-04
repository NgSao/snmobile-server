package com.snd.server.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerCategoryResponse {
    Long id;
    String name;
    String imageUrl;
    int displayOrder;

}
