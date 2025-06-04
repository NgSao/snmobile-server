package com.snd.server.event.domain;

import java.math.BigDecimal;

import com.snd.server.event.EventType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InventoryEvent {
    EventType eventType;
    String skuProduct;
    String skuVariant;
    Integer quantity;
    BigDecimal price;
}
