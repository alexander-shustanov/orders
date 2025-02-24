package ru.springio.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.springio.orders.domain.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
