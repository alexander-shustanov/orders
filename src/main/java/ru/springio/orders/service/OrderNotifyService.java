package ru.springio.orders.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.springio.orders.domain.Order;

@RequiredArgsConstructor
@Service
public class OrderNotifyService {
    //  private final KafkaTemplate<String, OrderDto> kafkaTemplate;

    public void orderPayed(Order order) {
        // kafkaTemplate.send("order-pay", order.getId());
    }
}
