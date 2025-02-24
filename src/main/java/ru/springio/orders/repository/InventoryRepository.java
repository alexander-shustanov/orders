package ru.springio.orders.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import ru.springio.orders.domain.City;
import ru.springio.orders.domain.Inventory;
import ru.springio.orders.domain.Product;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductAndCity(Product product, City city);

    Page<Inventory> findByProduct(Product product, Pageable pageable);
}
