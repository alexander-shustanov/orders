package ru.springio.orders.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.springio.orders.rest.dto.*;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderDto create(@RequestBody CreateOrderDto orderDto) {
        return orderService.create(orderDto);
    }

    @GetMapping("/active")
    public ResponseEntity<OrderDto> getActiveOrder(@RequestParam Long customerId) {
        return orderService.getActiveOrder(customerId)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/{orderId}/lines")
    public OrderLineDto addProduct(@PathVariable Long orderId, @RequestBody ProductAndAmount productAndAmount) {
        return orderService.addProduct(orderId, productAndAmount.productId(), productAndAmount.amount());
    }

    @PostMapping("/{orderId}/lines/{orderLineId}")
    public OrderLineDto changeProductsAmount(@PathVariable Long orderLineId, @RequestBody OrderLineAmount amount) {
        return orderService.changeProductsAmount(orderLineId, amount);
    }

    @DeleteMapping("/{orderId}/lines/{orderLineId}")
    public void deleteProduct(@PathVariable Long orderLineId) {
        orderService.deleteProduct(orderLineId);
    }

    @PostMapping("/{orderId}/pay")
    public OrderDto payOrder(@PathVariable Long orderId) {
        return orderService.payOrder(orderId);
    }

    @RequestMapping(value = "/{orderId}", method=RequestMethod.DELETE)
    public void deleteOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
    }
}

