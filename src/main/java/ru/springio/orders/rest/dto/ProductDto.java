package ru.springio.orders.rest.dto;

import lombok.Value;

import java.math.BigDecimal;

/**
 * DTO for {@link ru.springio.orders.domain.Product}
 */
@Value
public class ProductDto {
    Long id;
    String name;
    BigDecimal price;
}
