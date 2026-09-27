package com.nishanth.ecommerce.service;

import com.nishanth.ecommerce.dto.InventoryResponse;
import com.nishanth.ecommerce.dto.InventoryUpdateRequest;
import com.nishanth.ecommerce.entity.Product;
import com.nishanth.ecommerce.exception.ProductNotFoundException;
import com.nishanth.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final ProductRepository productRepository;

    public InventoryService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Get inventory for a product
    @Transactional(readOnly = true)
    public InventoryResponse getInventory(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: " + productId));

        return convertToResponse(product);
    }

    // Update stock
    @Transactional
    public InventoryResponse updateStock(
            Long productId,
            InventoryUpdateRequest request) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: " + productId));

        product.setStockQuantity(request.getStockQuantity());

        Product updatedProduct =
                productRepository.save(product);

        return convertToResponse(updatedProduct);
    }

    private InventoryResponse convertToResponse(Product product) {

        return new InventoryResponse(
                product.getId(),
                product.getName(),
                product.getStockQuantity(),
                product.getVersion(),
                product.getStatus()
        );
    }
}