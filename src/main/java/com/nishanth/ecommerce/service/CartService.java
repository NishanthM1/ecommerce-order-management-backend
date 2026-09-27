package com.nishanth.ecommerce.service;

import com.nishanth.ecommerce.dto.CartItemRequest;
import com.nishanth.ecommerce.dto.CartItemResponse;
import com.nishanth.ecommerce.dto.CartResponse;
import com.nishanth.ecommerce.entity.Cart;
import com.nishanth.ecommerce.entity.CartItem;
import com.nishanth.ecommerce.entity.Product;
import com.nishanth.ecommerce.entity.ProductStatus;
import com.nishanth.ecommerce.entity.User;
import com.nishanth.ecommerce.exception.*;
import com.nishanth.ecommerce.repository.CartItemRepository;
import com.nishanth.ecommerce.repository.CartRepository;
import com.nishanth.ecommerce.repository.ProductRepository;
import com.nishanth.ecommerce.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {

        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // ==========================================
    // GET OR CREATE CART
    // ==========================================

    private Cart getOrCreateCart(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {

                    Cart cart = new Cart();

                    cart.setUser(user);
                    cart.setItems(new ArrayList<>());

                    return cartRepository.save(cart);
                });
    }

    // ==========================================
    // GET CART
    // ==========================================

    @Transactional
    public CartResponse getCart(String email) {

        Cart cart = getOrCreateCart(email);

        return convertToResponse(cart);
    }

    // ==========================================
    // ADD PRODUCT TO CART
    // ==========================================

    @Transactional
    public CartResponse addItem(
            String email,
            CartItemRequest request) {

        Cart cart = getOrCreateCart(email);

        Product product = productRepository.findById(
                        request.getProductId())
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id: "
                                        + request.getProductId()));

        if (product.getStatus() != ProductStatus.ACTIVE) {

            throw new ProductInactiveException(
                    "Product is currently inactive"
            );
        }

        CartItem cartItem =
                cartItemRepository
                        .findByCartIdAndProductId(
                                cart.getId(),
                                product.getId())
                        .orElse(null);

        int newQuantity;

        if (cartItem == null) {

            newQuantity = request.getQuantity();

            if (newQuantity > product.getStockQuantity()) {

                throw new InvalidStockException(
                        "Insufficient stock. Available stock: "
                                + product.getStockQuantity()
                );
            }

            cartItem = new CartItem();

            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(newQuantity);

        } else {

            newQuantity =
                    cartItem.getQuantity()
                            + request.getQuantity();

            if (newQuantity > product.getStockQuantity()) {

                throw new RuntimeException(
                        "Insufficient stock. Available stock: "
                                + product.getStockQuantity());
            }

            cartItem.setQuantity(newQuantity);
        }

        cartItemRepository.save(cartItem);

        return convertToResponse(cart);
    }

    // ==========================================
    // UPDATE CART ITEM
    // ==========================================

    @Transactional
    public CartResponse updateItem(
            String email,
            Long productId,
            Integer quantity) {

        Cart cart = getOrCreateCart(email);

        CartItem cartItem =
                cartItemRepository
                        .findByCartIdAndProductId(
                                cart.getId(),
                                productId)
                        .orElseThrow(() ->
                                new CartItemNotFoundException("Product is not in the cart"));

        Product product = cartItem.getProduct();

        if (product.getStatus() != ProductStatus.ACTIVE) {

            throw new RuntimeException("Product is currently inactive");
        }

        if (quantity > product.getStockQuantity()) {

            throw new RuntimeException("Insufficient stock. Available stock: " + product.getStockQuantity());
        }

        cartItem.setQuantity(quantity);

        cartItemRepository.save(cartItem);

        return convertToResponse(cart);
    }

    // ==========================================
    // REMOVE ITEM
    // ==========================================

    @Transactional
    public CartResponse removeItem(
            String email,
            Long productId) {

        Cart cart = getOrCreateCart(email);

        CartItem cartItem =
                cartItemRepository
                        .findByCartIdAndProductId(
                                cart.getId(),
                                productId)
                        .orElseThrow(() ->
                                new CartItemNotFoundException(
                                        "Product is not in the cart"));

        cart.getItems().remove(cartItem);

        cartItemRepository.delete(cartItem);

        return convertToResponse(cart);
    }

    // ==========================================
    // CLEAR CART
    // ==========================================

    @Transactional
    public void clearCart(String email) {

        Cart cart = getOrCreateCart(email);

        cartItemRepository.deleteByCartId(cart.getId());

        cart.getItems().clear();
    }

    // ==========================================
    // CONVERT CART TO RESPONSE
    // ==========================================

    private CartResponse convertToResponse(Cart cart) {

        List<CartItemResponse> items = new ArrayList<>();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem item : cart.getItems()) {

            Product product = item.getProduct();

            BigDecimal subtotal =
                    product.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            item.getQuantity()
                                    )
                            );

            CartItemResponse itemResponse =
                    new CartItemResponse(
                            product.getId(),
                            product.getName(),
                            product.getPrice(),
                            item.getQuantity(),
                            subtotal
                    );

            items.add(itemResponse);

            totalAmount =
                    totalAmount.add(subtotal);
        }

        return new CartResponse(
                cart.getId(),
                cart.getUser().getId(),
                items,
                totalAmount
        );
    }
}