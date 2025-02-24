package ru.springio.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.springio.orders.domain.OrderLine;

public interface OrderLineRepository extends JpaRepository<OrderLine, Long> {
}
