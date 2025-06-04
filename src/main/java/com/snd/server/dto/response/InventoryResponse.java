package com.snd.server.dto.response;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventoryResponse extends BaseResponse {
    Long id;
    String skuProduct;
    String skuVariant;
    Integer quantity;
    BigDecimal importPrice;
    Instant lastUpdated;
}