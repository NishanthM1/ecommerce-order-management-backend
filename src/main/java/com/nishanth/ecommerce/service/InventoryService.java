package com.nishanth.ecommerce.service;

import com.nishanth.ecommerce.dto.InventoryResponse;
import com.nishanth.ecommerce.dto.InventoryUpdateRequest;
import com.nishanth.ecommerce.entity.Product;
import com.nishanth.ecommerce.exception.ProductNotFoundException;
import com.nishanth.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nishanth.ecommerce.exception.InvalidStockException;
import java.util.List;
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
    public List<InventoryResponse> getLowStockProducts(Integer threshold) {

        if (threshold < 0) {
            throw new InvalidStockException("Stock threshold cannot be negative");
        }

        return productRepository
                .findByStockQuantityLessThanEqual(threshold)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }
    @Transactional
    public void decreaseStock(Product product, int quantity) {

        if (quantity <= 0) {
            throw new InvalidStockException("Quantity must be greater than 0");
        }

        if (product.getStockQuantity() < quantity) {
            throw new InvalidStockException(
                    "Insufficient stock. Available stock: "
                            + product.getStockQuantity()
            );
        }

        product.setStockQuantity(
                product.getStockQuantity() - quantity
        );

        productRepository.save(product);
    }
    @Transactional
    public void increaseStock(Product product, int quantity) {

        if (quantity <= 0) {
            throw new InvalidStockException("Quantity must be greater than 0");
        }

        product.setStockQuantity(
                product.getStockQuantity() + quantity
        );

        productRepository.save(product);
    }
}