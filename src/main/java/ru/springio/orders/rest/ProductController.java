package ru.springio.orders.rest;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.springio.orders.domain.Product;
import ru.springio.orders.rest.dto.CreateProductDto;
import ru.springio.orders.rest.dto.ProductDto;
import ru.springio.orders.rest.mapper.ProductMapper;
import ru.springio.orders.service.ProductService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    private final ProductMapper productMapper;

    @GetMapping
    public PagedModel<ProductDto> getAll(Pageable pageable) {
        Page<Product> products = productService.getAll(pageable);
        return new PagedModel<>(
            products.map(productMapper::toProductDto)
        );
    }

    @Nullable
    @GetMapping("/{id}/photo")
    public String getProductPhotoUrl(@PathVariable Long id) {
        return productService.getProductPhotoUrl(id);
    }

    @GetMapping("/{id}")
    public ProductDto getOne(@PathVariable Long id) {
        return productMapper.toProductDto(productService.getOne(id));
    }

    @GetMapping("/by-ids")
    public List<ProductDto> getMany(@RequestParam List<Long> ids) {
        return productService.getMany(ids)
            .stream()
            .map(productMapper::toProductDto)
            .toList();
    }

    @PostMapping
    public ProductDto create(@RequestBody CreateProductDto product) {
        return productMapper.toProductDto(
            productService.create(product)
        );
    }

    @PostMapping("/{id}/rename")
    public ProductDto renameProduct(@PathVariable Long id, @RequestParam String name) {
        return productMapper.toProductDto(
            productService.rename(id, name)
        );
    }

    @PatchMapping("/{id}")
    public ProductDto patch(
        @PathVariable Long id,
        @RequestPart("data") JsonNode patchNode,
        @RequestPart(value = "file", required = false) MultipartFile imageFile
    ) throws IOException {
        Product product = productService.patch(id, patchNode, imageFile);
        return productMapper.toProductDto(product);
    }

    @PatchMapping
    public List<Long> patchMany(@RequestParam List<Long> ids, @RequestBody JsonNode patchNode) throws IOException {
        return productService.patchMany(ids, patchNode);
    }

    @DeleteMapping("/{id}")
    public ProductDto delete(@PathVariable Long id) {
        Product product = productService.delete(id);
        if (product != null) {
            return productMapper.toProductDto(product);
        } else {
            return null;
        }
    }

    @DeleteMapping
    public void deleteMany(@RequestParam List<Long> ids) {
        productService.deleteMany(ids);
    }
}
