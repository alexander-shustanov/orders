package ru.springio.orders.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.springio.orders.domain.Order;
import ru.springio.orders.rest.dto.*;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.springio.orders.rest.filter.OrderFilter;
import ru.springio.orders.rest.mapper.OrderLineMapper;
import ru.springio.orders.rest.mapper.OrderMapper;
import ru.springio.orders.service.OrderService;


@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    private final OrderLineMapper orderLineMapper;

    private final OrderMapper orderMapper;

    @PostMapping
    public OrderDto create(@RequestBody CreateOrderDto orderDto) {
        Order toCreate = orderMapper.toEntity(orderDto);
        Order created = orderService.create(toCreate);
        return orderMapper.toOrderDto(created);
    }

    @GetMapping("/active")
    public ResponseEntity<OrderWithLinesDto> getActiveOrder(@RequestParam Long customerId) {
        return orderService.getActiveOrder(customerId)
            .map(orderMapper::toOrderWithLinesDto)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping
    public PagedModel<OrderWithLinesDto> getAll(@ModelAttribute OrderFilter filter, Pageable pageable) {
        Page<OrderWithLinesDto> orderWithLinesDtos = orderService.getAll(filter, pageable);
        return new PagedModel<>(orderWithLinesDtos);
    }

    @PostMapping("/{orderId}/lines")
    public OrderLineDto addProduct(@PathVariable Long orderId, @RequestBody ProductAndAmount productAndAmount) {
        return orderLineMapper.toOrderLineDto(
            orderService.addProduct(
                orderId,
                productAndAmount.productId(),
                productAndAmount.amount()
            )
        );
    }

    @PostMapping("/{orderId}/lines/{orderLineId}")
    public OrderLineDto changeProductsAmount(@PathVariable Long orderLineId, @RequestBody OrderLineAmount amount) {
        return orderLineMapper.toOrderLineDto(
            orderService.changeProductsAmount(orderLineId, amount.getAmount())
        );
    }

    @DeleteMapping("/{orderId}/lines/{orderLineId}")
    public void deleteProduct(@PathVariable Long orderLineId) {
        orderService.deleteProduct(orderLineId);
    }

    @PostMapping("/{orderId}/pay")
    public OrderDto payOrder(@PathVariable Long orderId) {
        return orderMapper.toOrderDto(
            orderService.payOrder(orderId)
        );
    }

    @DeleteMapping("/{orderId}")
    public void deleteOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
    }
}

