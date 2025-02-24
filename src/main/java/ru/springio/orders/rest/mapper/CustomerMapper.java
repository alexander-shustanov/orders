package ru.springio.orders.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import ru.springio.orders.domain.Customer;
import ru.springio.orders.rest.dto.CreateCustomerDto;
import ru.springio.orders.rest.dto.CustomerDto;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface CustomerMapper {
    CustomerDto customerToCustomerDto(Customer customer);

    @Mapping(target = "id", ignore = true)
    Customer toEntity(CreateCustomerDto dto);

    Customer toEntity(CustomerDto customerDto);
}
