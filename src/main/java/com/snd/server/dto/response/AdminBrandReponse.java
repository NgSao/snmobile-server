package com.snd.server.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AdminBrandReponse extends BaseResponse {
    Long id;
    String name;
    String slug;
    String imageUrl;
    int displayOrder;
    int productCount;

}