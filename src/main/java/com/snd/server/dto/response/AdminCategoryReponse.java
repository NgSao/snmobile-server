package com.snd.server.dto.response;

import java.util.HashSet;
import java.util.Set;

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
public class AdminCategoryReponse extends BaseResponse {
    Long id;
    String name;
    String imageUrl;
    int productCount;
    int displayOrder;
    Long parentId;
    String parentName;
    Set<CategoryResponse> children = new HashSet<>();

}
