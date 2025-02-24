package ru.springio.orders.rest.dto;

import lombok.Value;

/**
 * DTO for {@link ru.springio.orders.domain.OrderLine}
 */
@Value
public class OrderLineAmount {
    Long amount;
}
