package ru.springio.orders.rest.dto;

import jakarta.validation.constraints.PositiveOrZero;

public record ProductAndAmount(Long productId, @PositiveOrZero Long amount) {
}
