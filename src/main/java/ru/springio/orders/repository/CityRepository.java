package ru.springio.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.springio.orders.domain.City;

public interface CityRepository extends JpaRepository<City, Long> {

}
