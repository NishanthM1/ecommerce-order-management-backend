package com.nishanth.ecommerce.dto;

import com.nishanth.ecommerce.entity.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InventoryResponse {

    private Long productId;
    private String productName;
    private Integer stockQuantity;
    private Long version;
    private ProductStatus status;
}