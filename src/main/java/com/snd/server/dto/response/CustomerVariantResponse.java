package com.snd.server.dto.response;

import java.math.BigDecimal;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerVariantResponse {
    Long id;
    String color;
    String storage;
    String sku;
    BigDecimal price;
    BigDecimal originalPrice;
    Integer stock;
    String image;
    int discount;

}
