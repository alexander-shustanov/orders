package ru.springio.orders.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Value;

import java.math.BigDecimal;

/**
 * DTO for {@link ru.springio.orders.domain.Product}
 */
@Value
public class CreateProductDto {
    @NotBlank
    String name;
    @Positive
    BigDecimal price;
}
