package ru.springio.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ru.springio.orders.domain.Customer;
import ru.springio.orders.domain.Order;
import ru.springio.orders.domain.OrderStatus;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    Optional<Order> findFirstByCustomerAndOrderStatusOrderByCreatedDateDesc(Customer customer, OrderStatus orderStatus);
}
