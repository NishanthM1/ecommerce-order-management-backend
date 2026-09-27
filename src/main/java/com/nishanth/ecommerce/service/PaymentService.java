package com.nishanth.ecommerce.service;

import com.nishanth.ecommerce.dto.PaymentResponse;
import com.nishanth.ecommerce.entity.Order;
import com.nishanth.ecommerce.entity.OrderStatus;
import com.nishanth.ecommerce.entity.Payment;
import com.nishanth.ecommerce.entity.PaymentStatus;
import com.nishanth.ecommerce.entity.User;
import com.nishanth.ecommerce.exception.*;
import com.nishanth.ecommerce.repository.OrderRepository;
import com.nishanth.ecommerce.repository.PaymentRepository;
import com.nishanth.ecommerce.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            UserRepository userRepository) {

        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    // ==========================================
    // GET PAYMENT BY ORDER
    // ==========================================

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrder(
            Long orderId,
            String email) {

        User user = getUser(email);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with id: "
                                        + orderId));

        // Customer can only access their own order
        if (!order.getUser().getId().equals(user.getId())) {
            throw new PaymentNotAllowedException(
                    "You cannot access this payment");
        }

        Payment payment = paymentRepository
                .findByOrderId(orderId)
                .orElseThrow(() ->
                        new PaymentNotFoundException(
                                "Payment not found for order: "
                                        + orderId));

        return convertToResponse(payment);
    }

    // ==========================================
    // PROCESS SUCCESSFUL PAYMENT
    // ==========================================

    @Transactional
    public PaymentResponse processPayment(
            Long orderId,
            String email) {

        User user = getUser(email);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with id: "
                                        + orderId));

        // Ownership check
        if (!order.getUser().getId().equals(user.getId())) {
            throw new PaymentNotAllowedException(
                    "You cannot make payment for this order");
        }

        Payment payment = paymentRepository
                .findByOrderId(orderId)
                .orElseThrow(() ->
                        new PaymentNotFoundException(
                                "Payment not found for order: "
                                        + orderId));

        // Already processed?
        if (payment.getStatus() != PaymentStatus.PENDING) {

            throw new PaymentAlreadyProcessedException(
                    "Payment has already been processed");
        }

        // Order must be payable
        if (order.getStatus() != OrderStatus.PLACED
                && order.getStatus() != OrderStatus.CONFIRMED) {

            throw new PaymentNotAllowedException(
                    "Payment is not allowed for order status: "
                            + order.getStatus());
        }

        // Simulated successful payment
        payment.setStatus(PaymentStatus.SUCCESS);

        payment.setTransactionReference(
                "TXN-" + UUID.randomUUID()
        );

        Payment savedPayment =
                paymentRepository.save(payment);

        return convertToResponse(savedPayment);
    }

    // ==========================================
    // SIMULATE FAILED PAYMENT
    // ==========================================

    @Transactional
    public PaymentResponse failPayment(
            Long orderId,
            String email) {

        User user = getUser(email);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with id: "
                                        + orderId));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new PaymentNotAllowedException(
                    "You cannot make payment for this order");
        }

        Payment payment = paymentRepository
                .findByOrderId(orderId)
                .orElseThrow(() ->
                        new PaymentNotFoundException(
                                "Payment not found for order: "
                                        + orderId));

        if (payment.getStatus() != PaymentStatus.PENDING) {

            throw new PaymentAlreadyProcessedException(
                    "Payment has already been processed");
        }

        if (order.getStatus() != OrderStatus.PLACED
                && order.getStatus() != OrderStatus.CONFIRMED) {

            throw new PaymentNotAllowedException(
                    "Payment is not allowed for order status: "
                            + order.getStatus());
        }

        payment.setStatus(PaymentStatus.FAILED);

        payment.setTransactionReference(
                "FAILED-" + UUID.randomUUID()
        );

        Payment savedPayment =
                paymentRepository.save(payment);

        return convertToResponse(savedPayment);
    }

    // ==========================================
    // ADMIN - GET ALL PAYMENTS
    // ==========================================

    @Transactional(readOnly = true)
    public Page<PaymentResponse> getAllPayments(
            Pageable pageable) {

        return paymentRepository
                .findAll(pageable)
                .map(this::convertToResponse);
    }

    // ==========================================
    // FIND USER
    // ==========================================

    private User getUser(String email) {

        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    // ==========================================
    // CONVERT TO RESPONSE
    // ==========================================

    private PaymentResponse convertToResponse(
            Payment payment) {

        return new PaymentResponse(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getTransactionReference(),
                payment.getCreatedAt()
        );
    }
}