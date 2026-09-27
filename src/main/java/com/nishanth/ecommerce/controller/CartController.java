package com.nishanth.ecommerce.controller;

import com.nishanth.ecommerce.dto.CartItemRequest;
import com.nishanth.ecommerce.dto.CartResponse;
import com.nishanth.ecommerce.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@PreAuthorize("hasRole('CUSTOMER')")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // ==========================================
    // GET CART
    // ==========================================

    @GetMapping
    public ResponseEntity<CartResponse> getCart(
            Authentication authentication) {

        CartResponse response =
                cartService.getCart(
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // ADD ITEM
    // ==========================================

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(
            Authentication authentication,
            @Valid @RequestBody CartItemRequest request) {

        CartResponse response =
                cartService.addItem(
                        authentication.getName(),
                        request
                );

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // UPDATE ITEM QUANTITY
    // ==========================================

    @PutMapping("/items/{productId}")
    public ResponseEntity<CartResponse> updateItem(
            Authentication authentication,
            @PathVariable Long productId,
            @RequestParam Integer quantity) {

        CartResponse response =
                cartService.updateItem(
                        authentication.getName(),
                        productId,
                        quantity
                );

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // REMOVE ITEM
    // ==========================================

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponse> removeItem(
            Authentication authentication,
            @PathVariable Long productId) {

        CartResponse response =
                cartService.removeItem(
                        authentication.getName(),
                        productId
                );

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // CLEAR CART
    // ==========================================

    @DeleteMapping
    public ResponseEntity<String> clearCart(
            Authentication authentication) {

        cartService.clearCart(
                authentication.getName()
        );

        return ResponseEntity.ok(
                "Cart cleared successfully"
        );
    }
}