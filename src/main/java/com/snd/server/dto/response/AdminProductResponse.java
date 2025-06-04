package com.snd.server.dto.response;

import java.math.BigDecimal;
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
public class AdminProductResponse extends BaseResponse {
    Long id;
    String name;
    String slug;
    String sku;
    String specification;
    String description;
    String promotions;
    BigDecimal originalPrice;
    BigDecimal salePrice;
    Integer stock;

    Integer sold;

    Integer rating;
    BrandResponse brand;
    Set<CategoryResponse> categories = new HashSet<>();
    Set<MediaResponse> images = new HashSet<>();
    Set<VariantResponse> variants = new HashSet<>();

}
