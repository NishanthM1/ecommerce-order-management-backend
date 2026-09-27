package com.nishanth.ecommerce.controller;

import com.nishanth.ecommerce.dto.PaymentResponse;
import com.nishanth.ecommerce.service.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // ==========================================
    // GET PAYMENT
    // ==========================================

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PaymentResponse> getPayment(
            Authentication authentication,
            @PathVariable Long orderId) {

        PaymentResponse response =
                paymentService.getPaymentByOrder(
                        orderId,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // SUCCESSFUL PAYMENT
    // ==========================================

    @PostMapping("/order/{orderId}/pay")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PaymentResponse> processPayment(
            Authentication authentication,
            @PathVariable Long orderId) {

        PaymentResponse response =
                paymentService.processPayment(
                        orderId,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // FAILED PAYMENT
    // ==========================================

    @PostMapping("/order/{orderId}/fail")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<PaymentResponse> failPayment(
            Authentication authentication,
            @PathVariable Long orderId) {

        PaymentResponse response =
                paymentService.failPayment(
                        orderId,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }

    // ==========================================
    // ADMIN - GET ALL PAYMENTS
    // ==========================================

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PaymentResponse>> getAllPayments(

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

        Page<PaymentResponse> payments =
                paymentService.getAllPayments(pageable);

        return ResponseEntity.ok(payments);
    }
}