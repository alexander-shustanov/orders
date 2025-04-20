package ru.springio.orders.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import ru.springio.orders.domain.Customer;
import ru.springio.orders.domain.Order;
import ru.springio.orders.domain.OrderStatus;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    @EntityGraph(attributePaths = {"customer", "city", "orderLines.product"})
    Optional<Order> findFirstByCustomerAndOrderStatus_OrderByCreatedDateDesc(Customer customer, OrderStatus orderStatus);

    @EntityGraph(attributePaths = {"orderLines.product"})
    @Query("FROM Order o WHERE o.id = :id")
    Order getWithOrderLines(Long id);
}
