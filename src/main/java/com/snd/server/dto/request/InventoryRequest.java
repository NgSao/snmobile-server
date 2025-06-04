package com.snd.server.dto.request;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryRequest {
    private String skuProduct;
    private String skuVariant;
    private Integer quantity;
    private BigDecimal importPrice;

}
