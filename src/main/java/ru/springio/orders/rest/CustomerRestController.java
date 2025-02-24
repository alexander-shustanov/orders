package ru.springio.orders.rest;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import ru.springio.orders.domain.Customer;
import ru.springio.orders.rest.dto.CreateCustomerDto;
import ru.springio.orders.rest.dto.CustomerDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import ru.springio.orders.service.CustomerService;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerRestController {
    private final CustomerService customerService;

    @GetMapping
    public PagedModel<Customer> getAll(@ModelAttribute CustomerFilter filter, Pageable pageable) {
        Page<Customer> customers = customerService.getAll(filter, pageable);
        return new PagedModel<>(customers);
    }

    @GetMapping("/{id}")
    public CustomerDto getOne(@PathVariable Long id) {
        return customerService.getOne(id);
    }

    @PostMapping
    public CustomerDto create(@RequestBody CreateCustomerDto dto) {
        return customerService.create(dto);
    }
}
