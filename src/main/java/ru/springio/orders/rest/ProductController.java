package ru.springio.orders.rest;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.*;
import ru.springio.orders.domain.Product;
import ru.springio.orders.service.ProductService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public PagedModel<Product> getAll(Pageable pageable) {
        Page<Product> products = productService.getAll(pageable);
        return new PagedModel<>(products);
    }

    @GetMapping("/{id}")
    public Product getOne(@PathVariable Long id) {
        return productService.getOne(id);
    }

    @GetMapping("/by-ids")
    public List<Product> getMany(@RequestParam List<Long> ids) {
        return productService.getMany(ids);
    }

    @PostMapping
    public Product create(@RequestBody Product product) {
        return productService.create(product);
    }

    @PatchMapping("/{id}")
    public Product patch(@PathVariable Long id, @RequestBody JsonNode patchNode) throws IOException {
        return productService.patch(id, patchNode);
    }

    @PatchMapping
    public List<Long> patchMany(@RequestParam List<Long> ids, @RequestBody JsonNode patchNode) throws IOException {
        return productService.patchMany(ids, patchNode);
    }

    @DeleteMapping("/{id}")
    public Product delete(@PathVariable Long id) {
        return productService.delete(id);
    }

    @DeleteMapping
    public void deleteMany(@RequestParam List<Long> ids) {
        productService.deleteMany(ids);
    }
}
