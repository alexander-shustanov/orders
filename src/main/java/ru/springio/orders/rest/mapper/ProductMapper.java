package ru.springio.orders.rest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.springio.orders.domain.Product;
import ru.springio.orders.rest.dto.ProductDto;
import ru.springio.orders.rest.dto.CreateProductDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {
    Product toEntity(ProductDto productDto);

    ProductDto toProductDto(Product product);

    Product toEntity(CreateProductDto createProductDto);

    CreateProductDto toCreateProductDto(Product product);
}
