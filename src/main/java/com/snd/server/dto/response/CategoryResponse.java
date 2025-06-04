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
public class CategoryResponse {
    Long id;
    String name;
    String slug;
    String imageUrl;
    Long parentId;
    int displayOrder;
    Set<CategoryResponse> children = new HashSet<>();

}
