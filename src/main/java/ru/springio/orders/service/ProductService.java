package ru.springio.orders.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import ru.springio.orders.domain.Product;
import ru.springio.orders.repository.ProductRepository;
import ru.springio.orders.rest.dto.CreateProductDto;
import ru.springio.orders.rest.mapper.ProductMapper;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    private final ObjectMapper objectMapper;

    private final ProductMapper productMapper;

    private final FileService fileService;

    public Page<Product> getAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Product getOne(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        return productOptional.orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));
    }

    public List<Product> getMany(List<Long> ids) {
        return productRepository.findAllById(ids);
    }

    public Product create(CreateProductDto product) {
        return productRepository.save(productMapper.toEntity(product));
    }

    public Product patch(Long id, JsonNode patchNode, @Nullable MultipartFile imageFile) throws IOException {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        objectMapper.readerForUpdating(product).readValue(patchNode);

        if (imageFile != null) {
            String picture = fileService.uploadFile(imageFile);
            product.setPicture(picture);
        }

        return productRepository.save(product);
    }

    public List<Long> patchMany(List<Long> ids, JsonNode patchNode) throws IOException {
        Collection<Product> products = productRepository.findAllById(ids);

        for (Product product : products) {
            objectMapper.readerForUpdating(product).readValue(patchNode);
        }

        List<Product> resultProducts = productRepository.saveAll(products);
        return resultProducts.stream()
                .map(Product::getId)
                .toList();
    }

    public Product delete(Long id) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            productRepository.delete(product);
        }
        return product;
    }

    public void deleteMany(List<Long> ids) {
        productRepository.deleteAllById(ids);
    }

    @Transactional
    public Product rename(Long id, String name) {
        Product product = productRepository.findById(id).orElseThrow();
        product.setName(name);
        return product;
    }

    public String getProductPhotoUrl(Long id) {
        Product product = productRepository.findById(id).orElseThrow();

        if (product.getPicture() == null) {
            return null;
        }

        return fileService.fileUrl(product.getPicture());
    }
}
