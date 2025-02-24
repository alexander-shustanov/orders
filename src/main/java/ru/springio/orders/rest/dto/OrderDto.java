package ru.springio.orders.rest.dto;

import lombok.Value;
import ru.springio.orders.domain.OrderStatus;

import java.math.BigDecimal;

/**
 * DTO for {@link ru.springio.orders.domain.Order}
 */
@Value
public class OrderDto {
    Long id;
    Long customerId;
    OrderStatus orderStatus;
    BigDecimal sum;
}
