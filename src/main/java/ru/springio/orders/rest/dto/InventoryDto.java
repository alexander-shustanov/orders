package ru.springio.orders.rest.dto;

import lombok.Value;

/**
 * DTO for {@link ru.springio.orders.domain.Inventory}
 */
@Value
public class InventoryDto {
    Long id;
    Long productId;
    Long available;
    Long reserved;
    CityDto city;

//    demo: Add this during demo
    /**
     * DTO for {@link ru.springio.orders.domain.City}
     */
    @Value
    public static class CityDto {
        Long id;
        String name;
    }
}
