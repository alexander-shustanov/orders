package ru.springio.orders.rest.mapper;

import org.mapstruct.*;
import ru.springio.orders.domain.Order;
import ru.springio.orders.rest.dto.OrderWithLinesDto;
import ru.springio.orders.rest.dto.CreateOrderDto;
import ru.springio.orders.rest.dto.OrderDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING, uses = {OrderLineMapper.class})
public interface OrderMapper {
    @Mapping(source = "customer.id", target = "customerId")
    OrderDto toOrderDto(Order order);

    Order toEntity(OrderDto orderDto);

    @Mapping(source = "customerId", target = "customer.id")
    @Mapping(source = "cityId", target = "city.id")
    Order toEntity(CreateOrderDto createOrderDto);

    @Mapping(source = "customerId", target = "customer.id")
    Order toEntity(OrderWithLinesDto orderWithLinesDto);

    @AfterMapping
    default void linkOrderLines(@MappingTarget Order order) {
        order.getOrderLines().forEach(orderLine -> orderLine.setOrder(order));
    }

    @InheritInverseConfiguration(name = "toEntity")
    OrderWithLinesDto toOrderWithLinesDto(Order order);
}
