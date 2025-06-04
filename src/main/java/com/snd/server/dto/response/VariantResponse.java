package com.snd.server.dto.response;

import java.math.BigDecimal;

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
public class VariantResponse extends BaseResponse {
    Long id;
    String color;

    String size;

    String sku;
    String slug;

    String imageUrl;

    BigDecimal originalPrice;

    BigDecimal salePrice;

    Integer stockQuantity;

    Integer displayOrder;

}
