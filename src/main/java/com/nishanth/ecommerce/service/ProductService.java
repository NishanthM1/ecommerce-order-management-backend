package com.nishanth.ecommerce.service;

import com.nishanth.ecommerce.dto.ProductRequest;
import com.nishanth.ecommerce.dto.ProductResponse;
import com.nishanth.ecommerce.entity.Category;
import com.nishanth.ecommerce.entity.Product;
import com.nishanth.ecommerce.entity.ProductStatus;
import com.nishanth.ecommerce.exception.CategoryNotFoundException;
import com.nishanth.ecommerce.exception.ProductNotFoundException;
import com.nishanth.ecommerce.repository.CategoryRepository;
import com.nishanth.ecommerce.repository.ProductRepository;
import com.nishanth.ecommerce.repository.ProductSpecification;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository) {

        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // =========================
    // CREATE PRODUCT
    // =========================

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new CategoryNotFoundException(
                                "Category not found with id: "
                                        + request.getCategoryId()));

        Product product = new Product();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setStatus(ProductStatus.ACTIVE);
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);

        return convertToResponse(savedProduct);
    }

    // =========================
    // GET PRODUCT BY ID
    // =========================

    @Transactional(readOnly = true)
    @Cacheable(value = "products", key = "#id")
    public ProductResponse getProductById(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: " + id));

        return convertToResponse(product);
    }

    // =========================
    // GET PRODUCTS
    // =========================

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProducts(
            String search,
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            ProductStatus status,
            int page,
            int size,
            String sortBy,
            String direction
    ) {

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(
                page,
                size,
                sort
        );

        Specification<Product> specification =
                ProductSpecification.filterProducts(
                        search,
                        categoryId,
                        minPrice,
                        maxPrice,
                        status
                );

        return productRepository
                .findAll(specification, pageable)
                .map(this::convertToResponse);
    }

    // =========================
    // UPDATE PRODUCT
    // =========================

    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public ProductResponse updateProduct(
            Long id,
            ProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: " + id));

        Category category = categoryRepository.findById(
                        request.getCategoryId())
                .orElseThrow(() ->
                        new CategoryNotFoundException(
                                "Category not found with id: "
                                        + request.getCategoryId()));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(category);

        Product updatedProduct =
                productRepository.save(product);

        return convertToResponse(updatedProduct);
    }

    // =========================
    // DEACTIVATE PRODUCT
    // =========================

    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public ProductResponse deactivateProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: " + id));

        product.setStatus(ProductStatus.INACTIVE);

        Product updatedProduct =
                productRepository.save(product);

        return convertToResponse(updatedProduct);
    }

    // =========================
    // CONVERT ENTITY TO DTO
    // =========================

    private ProductResponse convertToResponse(Product product) {

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getStatus(),
                product.getCategory().getId(),
                product.getCategory().getName()
        );
    }
}