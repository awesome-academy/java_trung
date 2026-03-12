package com.example.foodsdrinks.service;

import com.example.foodsdrinks.dto.request.AddToCartRequest;
import com.example.foodsdrinks.dto.request.UpdateCartItemRequest;
import com.example.foodsdrinks.dto.response.CartItemResponse;
import com.example.foodsdrinks.dto.response.CartResponse;
import com.example.foodsdrinks.entity.CartItem;
import com.example.foodsdrinks.entity.Product;
import com.example.foodsdrinks.entity.User;
import com.example.foodsdrinks.exception.AppException;
import com.example.foodsdrinks.exception.ErrorCode;
import com.example.foodsdrinks.mapper.CartItemMapper;
import com.example.foodsdrinks.repository.CartItemRepository;
import com.example.foodsdrinks.repository.ProductRepository;
import com.example.foodsdrinks.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartItemMapper cartItemMapper;

    @Transactional(readOnly = true)
    public CartResponse getCart(String userId) {
        List<CartItemResponse> items = cartItemRepository.findAllByUserId(userId)
                .stream()
                .map(cartItemMapper::toResponse)
                .toList();

        BigDecimal totalAmount = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .items(items)
                .totalAmount(totalAmount)
                .build();
    }

    public CartResponse addToCart(String userId, AddToCartRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        if (!product.isAvailable()) {
            throw new AppException(ErrorCode.PRODUCT_UNAVAILABLE);
        }

        cartItemRepository.findByUserIdAndProductId(userId, product.getId())
                .ifPresentOrElse(
                        existing -> {
                            existing.setQuantity(existing.getQuantity() + request.getQuantity());
                            cartItemRepository.save(existing);
                        },
                        () -> {
                            User userRef = userRepository.getReferenceById(userId);
                            CartItem newItem = CartItem.builder()
                                    .user(userRef)
                                    .product(product)
                                    .quantity(request.getQuantity())
                                    .build();
                            cartItemRepository.save(newItem);
                        }
                );

        return getCart(userId);
    }

    public CartResponse updateCartItem(String userId, Long productId, UpdateCartItemRequest request) {
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);

        return getCart(userId);
    }

    public void removeCartItem(String userId, Long productId) {
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

        cartItemRepository.delete(cartItem);
    }

    public void clearCart(String userId) {
        cartItemRepository.deleteAllByUserId(userId);
    }
}
