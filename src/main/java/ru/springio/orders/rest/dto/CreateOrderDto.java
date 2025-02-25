package ru.springio.orders.rest.dto;

import lombok.Value;

/**
 * DTO for {@link ru.springio.orders.domain.Order}
 */
public record CreateOrderDto(Long customerId, Long cityId) {
}
