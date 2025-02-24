package ru.springio.orders.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.springio.orders.domain.Customer;
import ru.springio.orders.repository.CustomerRepository;
import ru.springio.orders.rest.CustomerFilter;
import ru.springio.orders.rest.dto.CreateCustomerDto;
import ru.springio.orders.rest.dto.CustomerDto;
import ru.springio.orders.rest.mapper.CustomerMapper;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;

    public Page<Customer> getAll(CustomerFilter filter, Pageable pageable) {
        Specification<Customer> spec = filter.toSpecification();
        return customerRepository.findAll(spec, pageable);
    }

    public CustomerDto create(CreateCustomerDto dto) {
        Customer customer = customerMapper.toEntity(dto);
        Customer resultCustomer = customerRepository.save(customer);
        return customerMapper.customerToCustomerDto(resultCustomer);
    }

    public CustomerDto getOne(Long id) {
        Optional<Customer> customerOptional = customerRepository.findById(id);
        return customerMapper.customerToCustomerDto(customerOptional.orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id))));
    }
}
