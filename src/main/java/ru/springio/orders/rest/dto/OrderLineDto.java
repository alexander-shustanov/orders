package ru.springio.orders.rest.dto;

import lombok.Value;

import java.math.BigDecimal;

/**
 * DTO for {@link ru.springio.orders.domain.OrderLine}
 */
@Value
public class OrderLineDto {
    Long id;
    Long productId;
    Long amount;
    Long orderId;
}
