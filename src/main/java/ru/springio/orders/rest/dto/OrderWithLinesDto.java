package ru.springio.orders.rest.dto;

import lombok.Value;
import ru.springio.orders.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * DTO for {@link ru.springio.orders.domain.Order}
 */
@Value
public class OrderWithLinesDto {
    Long id;
    OrderStatus orderStatus;
    List<OrderLineDto> orderLines;
    BigDecimal sum;
    CityDto city;
    Instant createdDate;

    /**
     * DTO for {@link ru.springio.orders.domain.OrderLine}
     */
    @Value
    public static class OrderLineDto {
        Long id;
        ProductDto product;
        Long amount;

        /**
         * DTO for {@link ru.springio.orders.domain.Product}
         */
        @Value
        public static class ProductDto {
            Long id;
            String name;
            BigDecimal price;
        }
    }

    /**
     * DTO for {@link ru.springio.orders.domain.City}
     */
    @Value
    public static class CityDto {
        Long id;
        String name;
    }
}
