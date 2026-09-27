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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            Pageable pageable) {

        Page<Product> products;

        boolean hasSearch =
                search != null && !search.trim().isEmpty();

        boolean hasCategory =
                categoryId != null;

        if (hasSearch && hasCategory) {

            products =
                    productRepository
                            .findByNameContainingIgnoreCaseAndCategoryId(
                                    search.trim(),
                                    categoryId,
                                    pageable
                            );

        } else if (hasSearch) {

            products =
                    productRepository
                            .findByNameContainingIgnoreCase(
                                    search.trim(),
                                    pageable
                            );

        } else if (hasCategory) {

            products =
                    productRepository.findByCategoryId(
                            categoryId,
                            pageable
                    );

        } else {

            products =
                    productRepository.findAll(pageable);
        }

        return products.map(this::convertToResponse);
    }

    // =========================
    // UPDATE PRODUCT
    // =========================

    @Transactional
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