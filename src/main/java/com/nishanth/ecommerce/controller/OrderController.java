package com.nishanth.ecommerce.controller;

import com.nishanth.ecommerce.dto.OrderResponse;
import com.nishanth.ecommerce.entity.OrderStatus;
import com.nishanth.ecommerce.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // ==========================================
    // CREATE ORDER
    // ==========================================

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> createOrder(
            Authentication authentication) {

        OrderResponse response =
                orderService.createOrder(
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // ==========================================
    // GET MY ORDERS
    // ==========================================

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Page<OrderResponse>> getMyOrders(
            Authentication authentication,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("createdAt").descending()
                );

        Page<OrderResponse> orders =
                orderService.getMyOrders(
                        authentication.getName(),
                        pageable
                );

        return ResponseEntity.ok(orders);
    }

    // ==========================================
    // GET MY ORDER BY ID
    // ==========================================

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> getOrderById(
            Authentication authentication,
            @PathVariable Long id) {

        OrderResponse response =
                orderService.getOrderById(
                        id,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // ADMIN - GET ALL ORDERS
    // ==========================================

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderResponse>> getAllOrders(

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("createdAt").descending()
                );

        Page<OrderResponse> orders =
                orderService.getAllOrders(pageable);

        return ResponseEntity.ok(orders);
    }

    // ==========================================
    // ADMIN - UPDATE STATUS
    // ==========================================

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {

        OrderResponse response =
                orderService.updateOrderStatus(
                        id,
                        status
                );

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // CUSTOMER - CANCEL ORDER
    // ==========================================

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> cancelOrder(
            Authentication authentication,
            @PathVariable Long id) {

        OrderResponse response =
                orderService.cancelOrder(
                        id,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }
}