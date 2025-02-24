package ru.springio.orders.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.springio.orders.domain.Customer;
import ru.springio.orders.domain.Order;
import ru.springio.orders.domain.OrderStatus;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findFirstByCustomerAndOrderStatusOrderByCreatedDateDesc(Customer customer, OrderStatus orderStatus);

    Page<Order> findByCustomer(Customer customer, Pageable pageable);
}
