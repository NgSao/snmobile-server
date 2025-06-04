package com.snd.server.dto.response;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerProductResponse {
    Long id;
    String name;
    String slug;
    String sku;
    String specification;
    String description;
    String promotions;
    BigDecimal originalPrice;
    BigDecimal salePrice;
    CustomerBrandResponse brand;
    int discount;
    float rating;
    int ratingCount;
    Integer stock;
    Integer sold;
    Set<CustomerVariantResponse> variants = new HashSet<>();
    Set<String> images = new HashSet<>();
    Set<CustomerCategoryResponse> categories = new HashSet<>();
}
