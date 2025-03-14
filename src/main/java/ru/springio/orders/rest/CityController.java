package ru.springio.orders.rest;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import ru.springio.orders.domain.City;
import ru.springio.orders.repository.CityRepository;

@RestController
@RequestMapping("/cities")
@RequiredArgsConstructor
public class CityController {

    private final CityRepository cityRepository;

    @GetMapping
    public List<City> findAll() {
        return cityRepository.findAll();
    }

    @PostMapping
    public City create(@RequestBody City city) {
        return cityRepository.save(city);
    }
}
