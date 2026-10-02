package com.nishanth.ecommerce.service;

import com.nishanth.ecommerce.dto.OrderItemResponse;
import com.nishanth.ecommerce.dto.OrderResponse;
import com.nishanth.ecommerce.entity.*;
import com.nishanth.ecommerce.exception.*;
import com.nishanth.ecommerce.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final InventoryService inventoryService;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository,
            PaymentRepository paymentRepository,
            InventoryService inventoryService) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.inventoryService = inventoryService;
    }

    // ==========================================
    // CREATE ORDER
    // ==========================================

    @Transactional
    public OrderResponse createOrder(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
         new UserNotFoundException("User not found"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        new EmptyCartException("Cart is empty"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new EmptyCartException("Cart is empty");
        }

        Order order = new Order();

        order.setUser(user);
        order.setStatus(OrderStatus.PLACED);
        order.setItems(new ArrayList<>());

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {

            Product product = cartItem.getProduct();

            // Check product status
            if (product.getStatus() != ProductStatus.ACTIVE) {
                throw new ProductInactiveException(
                        "Product is inactive: " + product.getName()
                );
            }

            // Check stock
            if (cartItem.getQuantity() > product.getStockQuantity()) {
                throw new InsufficientStockException(
                        "Insufficient stock for product: "
                                + product.getName()
                                + ". Available: "
                                + product.getStockQuantity());
            }

            // Calculate subtotal
            BigDecimal subtotal =
                    product.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            cartItem.getQuantity()
                                    )
                            );

            // Create order item
            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());

            // Snapshot current price
            orderItem.setUnitPrice(product.getPrice());

            orderItem.setSubtotal(subtotal);

            order.getItems().add(orderItem);

            totalAmount = totalAmount.add(subtotal);

            // Reduce stock
            inventoryService.decreaseStock(
                    product,
                    cartItem.getQuantity()
            );

            productRepository.save(product);
        }

        order.setTotalAmount(totalAmount);

        // Save order
        Order savedOrder = orderRepository.save(order);

        // Save order items
        orderItemRepository.saveAll(savedOrder.getItems());

        // Create payment
        Payment payment = new Payment();

        payment.setOrder(savedOrder);
        payment.setAmount(totalAmount);
        payment.setStatus(PaymentStatus.PENDING);

        paymentRepository.save(payment);

        // Clear cart
        cartItemRepository.deleteByCartId(cart.getId());
        cart.getItems().clear();

        return convertToResponse(savedOrder);
    }

    // ==========================================
    // GET CUSTOMER ORDERS
    // ==========================================

    @Transactional(readOnly = true)
    public Page<OrderResponse> getMyOrders(
            String email,
            Pageable pageable) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->new UserNotFoundException("User not found"));

        Page<Order> orders =
                orderRepository.findByUserId(
                        user.getId(),
                        pageable
                );

        return orders.map(this::convertToResponse);
    }

    // ==========================================
    // GET SINGLE ORDER
    // ==========================================

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(
            Long orderId,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->new UserNotFoundException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with id: "
                                        + orderId));

        // Customer can only see own order
        if (!order.getUser().getId().equals(user.getId())) {
            throw new OrderNotFoundException(
                    "Order not found with id: " + orderId);
        }

        return convertToResponse(order);
    }

    // ==========================================
    // ADMIN - GET ALL ORDERS
    // ==========================================

    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(
            Pageable pageable) {

        return orderRepository
                .findAll(pageable)
                .map(this::convertToResponse);
    }

    // ==========================================
    // ADMIN - UPDATE ORDER STATUS
    // ==========================================

    @Transactional
    public OrderResponse updateOrderStatus(
            Long orderId,
            OrderStatus newStatus) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with id: "
                                        + orderId));

        validateStatusTransition(
                order.getStatus(),
                newStatus
        );

        order.setStatus(newStatus);

        Order updatedOrder =
                orderRepository.save(order);

        return convertToResponse(updatedOrder);
    }

    // ==========================================
    // CUSTOMER - CANCEL ORDER
    // ==========================================

    @Transactional
    public OrderResponse cancelOrder(
            Long orderId,
            String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->new UserNotFoundException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found with id: "
                                        + orderId));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new OrderNotFoundException(
                    "Order not found with id: " + orderId);
        }

        if (order.getStatus() != OrderStatus.PLACED
                && order.getStatus() != OrderStatus.CONFIRMED) {

            throw new InvalidOrderStatusException(
                    "Order cannot be cancelled in status: "
                            + order.getStatus());
        }

        // Return stock
        for (OrderItem item : order.getItems()) {

            Product product = item.getProduct();

            inventoryService.increaseStock(
                    product,
                    item.getQuantity()
            );
            productRepository.save(product);
        }

        order.setStatus(OrderStatus.CANCELLED);

        Order cancelledOrder =
                orderRepository.save(order);

        return convertToResponse(cancelledOrder);
    }

    // ==========================================
    // STATUS TRANSITION VALIDATION
    // ==========================================

    private void validateStatusTransition(
            OrderStatus current,
            OrderStatus next) {

        boolean valid = false;

        if (current == OrderStatus.PLACED
                && (next == OrderStatus.CONFIRMED
                || next == OrderStatus.CANCELLED)) {

            valid = true;

        } else if (current == OrderStatus.CONFIRMED
                && next == OrderStatus.PROCESSING) {

            valid = true;

        } else if (current == OrderStatus.PROCESSING
                && next == OrderStatus.SHIPPED) {

            valid = true;

        } else if (current == OrderStatus.SHIPPED
                && next == OrderStatus.DELIVERED) {

            valid = true;
        }

        if (!valid) {
            throw new InvalidOrderStatusException(
                    "Invalid order status transition from "
                            + current
                            + " to "
                            + next);
        }
    }

    // ==========================================
    // CONVERT TO RESPONSE
    // ==========================================

    private OrderResponse convertToResponse(Order order) {

        List<OrderItemResponse> items =
                new ArrayList<>();

        for (OrderItem item : order.getItems()) {

            Product product = item.getProduct();

            OrderItemResponse response =
                    new OrderItemResponse(
                            product.getId(),
                            product.getName(),
                            item.getQuantity(),
                            item.getUnitPrice(),
                            item.getSubtotal()
                    );

            items.add(response);
        }

        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getTotalAmount(),
                order.getStatus(),
                items,
                order.getCreatedAt()
        );
    }
}