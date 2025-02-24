package ru.springio.orders.rest.mapper;

import org.mapstruct.*;
import ru.springio.orders.domain.Order;
import ru.springio.orders.domain.OrderLine;
import ru.springio.orders.rest.dto.CreateOrderDto;
import ru.springio.orders.rest.dto.OrderDto;
import ru.springio.orders.rest.dto.OrderLineDto;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {
    @Mapping(source = "customer.id", target = "customerId")
    @Mapping(source = "orderLines", qualifiedByName = "toOrderLinesDto", target = "orderLines")
    OrderDto toOrderDto(Order order);

    Order toEntity(OrderDto orderDto);

    @Mapping(source = "customerId", target = "customer.id")
    @Mapping(source = "cityId", target = "city.id")
    Order toEntity(CreateOrderDto createOrderDto);

    @Mapping(source = "customer.id", target = "customerId")
    CreateOrderDto toCreateOrderDto(Order order);

    @Named("toOrderLinesDto")
    default List<OrderDto.OrderLineDto> toOrderLinesDto(List<OrderLine> orderLines) {
        return orderLines.stream()
            .map(line ->
                new OrderDto.OrderLineDto(
                    line.getId(),
                    line.getProduct().getId(),
                    line.getAmount()
                )
            )
            .toList();
    }
}
