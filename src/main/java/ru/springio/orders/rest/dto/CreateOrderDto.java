package ru.springio.orders.rest.dto;

import lombok.Value;

/**
 * DTO for {@link ru.springio.orders.domain.Order}
 */
@Value
public class CreateOrderDto {
    Long customerId;
    Long cityId;
}
