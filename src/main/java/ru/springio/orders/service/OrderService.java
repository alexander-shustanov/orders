package ru.springio.orders.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.springio.orders.domain.*;
import ru.springio.orders.repository.*;
import ru.springio.orders.rest.dto.OrderWithLinesDto;
import ru.springio.orders.rest.dto.CreateOrderDto;
import ru.springio.orders.rest.dto.OrderDto;
import ru.springio.orders.rest.dto.OrderLineAmount;
import ru.springio.orders.rest.dto.OrderLineDto;
import ru.springio.orders.rest.filter.OrderFilter;
import ru.springio.orders.rest.mapper.OrderLineMapper;
import ru.springio.orders.rest.mapper.OrderMapper;
import ru.springio.orders.service.dto.OrderDeliveryInfoDto;

import java.math.BigDecimal;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    private final CustomerRepository customerRepository;

    private final ProductRepository productRepository;

    private final OrderLineRepository orderLineRepository;

//    private final KafkaTemplate<String, OrderDto> kafkaTemplate;

    private final InventoryService inventoryService;

    private final OrderNotifyService orderNotifyService;

    public Order create(Order order) {
        Optional<Order> existing = orderRepository.findFirstByCustomerAndOrderStatus_OrderByCreatedDateDesc(
            customerRepository.getReferenceById(order.getCustomer().getId()),
            OrderStatus.NEW
        );

        if (existing.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        return orderRepository.save(order);
    }

    public Optional<Order> getActiveOrder(Long customerId) {
        Customer customer = customerRepository.getReferenceById(customerId);

        return orderRepository.findFirstByCustomerAndOrderStatus_OrderByCreatedDateDesc(customer, OrderStatus.NEW);
    }

    public OrderLine addProduct(Long orderId, Long productId, Long amount) {
        Order order = orderRepository.getWithOrderLines(orderId);

        if (order.getOrderStatus() != OrderStatus.NEW) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Order lines is readonly");
        }

        Product product = productRepository.getReferenceById(productId);

        inventoryService.reserve(product, order.getCity(), amount);

        OrderLine orderLine = order.getOrderLines().stream()
            .filter(line -> line.getProduct().getId().equals(productId))
            .findFirst()
            .orElseGet(() -> {
                OrderLine line = new OrderLine();
                line.setOrder(order);
                line.setProduct(product);
                line.setAmount(0L);
                order.getOrderLines().add(line);
                return line;
            });

        orderLine.setAmount(orderLine.getAmount() + amount);
        orderLineRepository.saveAndFlush(orderLine);

        recalculateOrderSum(order);

        orderRepository.saveAndFlush(order);

        return orderLine;
    }

    @Transactional
    public OrderLine changeProductsAmount(Long orderLineId, Long amount) {
        OrderLine orderLine = orderLineRepository.findById(orderLineId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Order order = orderLine.getOrder();
        if (order.getOrderStatus() != OrderStatus.NEW) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        Product product = productRepository.getReferenceById(orderLine.getProduct().getId());

        if (amount > orderLine.getAmount()) {
            inventoryService.reserve(product, order.getCity(), amount - orderLine.getAmount());
        } else {
            inventoryService.cancelReserve(product, order.getCity(), orderLine.getAmount() - amount);
        }

        orderLine.setAmount(amount);

        orderLineRepository.saveAndFlush(orderLine);

        order = orderLine.getOrder();
        recalculateOrderSum(order);

        orderRepository.save(order);

        return orderLine;
    }

    @Transactional
    public void deleteProduct(Long orderLineId) {
        orderLineRepository.findById(orderLineId)
            .ifPresent(line -> {
                inventoryService.cancelReserve(
                    line.getProduct(),
                    line.getOrder().getCity(),
                    line.getAmount()
                );

                recalculateOrderSum(line.getOrder());
            });

        orderLineRepository.deleteById(orderLineId);
    }

    @Transactional
    public Order payOrder(Long orderId) {
        Order order = getOrderOrThrow(orderId);
        if (order.getOrderStatus() != OrderStatus.NEW) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        order.setOrderStatus(OrderStatus.PAID);

        orderNotifyService.orderPayed(order);

//        kafkaTemplate.send("order-pay", orderDto);

        return order;
    }

    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = getOrderOrThrow(orderId);
        if (order.getOrderStatus() != OrderStatus.NEW) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        order
            .getOrderLines()
            .forEach(line -> {
                inventoryService.cancelReserve(
                    line.getProduct(),
                    order.getCity(),
                    line.getAmount()
                );
            });

        order.setOrderStatus(OrderStatus.CANCELED);
    }

    //    @KafkaListener(topics = "orderDelivery", containerFactory = "orderDeliveryInfoDtoListenerFactory")
//    @Transactional
    public void consumeOrderDeliveryInfoDto(OrderDeliveryInfoDto orderDeliveryInfoDto) {
        Order order = getOrderOrThrow(orderDeliveryInfoDto.id());
        if (orderDeliveryInfoDto.delivered()) {
            order.setOrderStatus(OrderStatus.DELIVERED);

            order
                .getOrderLines()
                .forEach(line -> {
                    inventoryService.productShipped(line.getProduct(), order.getCity(), line.getAmount());
                });
        } else {
            order.setOrderStatus(OrderStatus.CANCELED);

            order
                .getOrderLines()
                .forEach(line -> {
                    inventoryService.cancelReserve(line.getProduct(), order.getCity(), line.getAmount());
                });
        }
    }

    private Order getOrderOrThrow(Long orderId) {
        return orderRepository.findById(orderId)
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Order with id %s not found".formatted(orderId))
            );
    }

    public Page<OrderWithLinesDto> getAll(OrderFilter filter, Pageable pageable) {
        Specification<Order> spec = filter.toSpecification();
        Page<Order> orders = orderRepository.findAll(spec, pageable);
        return orders.map(orderMapper::toOrderWithLinesDto);
    }

    private void recalculateOrderSum(Order order) {
        order.setSum(
            order.getOrderLines().stream()
                .map(orderLine -> orderLine.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(orderLine.getAmount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }
}
