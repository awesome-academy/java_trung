package com.example.foodsdrinks.service;

import com.example.foodsdrinks.dto.request.CreateOrderRequest;
import com.example.foodsdrinks.dto.request.UpdateOrderStatusRequest;
import com.example.foodsdrinks.dto.response.OrderResponse;
import com.example.foodsdrinks.dto.response.OrderSummaryResponse;
import com.example.foodsdrinks.entity.CartItem;
import com.example.foodsdrinks.entity.Order;
import com.example.foodsdrinks.entity.OrderItem;
import com.example.foodsdrinks.entity.Product;
import com.example.foodsdrinks.entity.User;
import com.example.foodsdrinks.entity.enums.OrderStatus;
import com.example.foodsdrinks.exception.AppException;
import com.example.foodsdrinks.exception.ErrorCode;
import com.example.foodsdrinks.mapper.OrderMapper;
import com.example.foodsdrinks.repository.CartItemRepository;
import com.example.foodsdrinks.repository.OrderRepository;
import com.example.foodsdrinks.repository.ProductRepository;
import com.example.foodsdrinks.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private static final Set<OrderStatus> USER_CANCELLABLE = Set.of(OrderStatus.PENDING, OrderStatus.CONFIRMED);

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;

    public OrderResponse createOrder(String userId, CreateOrderRequest request) {
        List<CartItem> cartItems = cartItemRepository.findAllByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        // Validate availability early for clearer error messages.
        for (CartItem cartItem : cartItems) {
            if (!cartItem.getProduct().isAvailable()) {
                throw new AppException(ErrorCode.PRODUCT_UNAVAILABLE);
            }
        }

        // Calculate total amount
        BigDecimal totalAmount = cartItems.stream()
                .map(ci -> ci.getProduct().getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Build order
        User userRef = userRepository.getReferenceById(userId);
        Order order = Order.builder()
                .user(userRef)
                .totalAmount(totalAmount)
                .deliveryAddress(request.getDeliveryAddress())
                .note(request.getNote())
                .build();

        // Build order items, deduct stock with guarded atomic updates.
        List<OrderItem> orderItems = new ArrayList<>();
        cartItems.stream()
                .sorted(Comparator.comparing(ci -> ci.getProduct().getId()))
                .forEach(cartItem -> {
                    Product product = cartItem.getProduct();
                    int qty = cartItem.getQuantity();

                    int updated = productRepository.decrementStock(product.getId(), qty);
                    if (updated == 0) {
                        throw new AppException(ErrorCode.INSUFFICIENT_STOCK, product.getName());
                    }

                    BigDecimal unitPrice = product.getPrice();
                    BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(qty));

                    OrderItem orderItem = OrderItem.builder()
                            .order(order)
                            .product(product)
                            .quantity(qty)
                            .unitPrice(unitPrice)
                            .subtotal(subtotal)
                            .build();
                    orderItems.add(orderItem);
                });

        order.setOrderItems(orderItems);
        Order savedOrder = orderRepository.save(order);

        cartItemRepository.deleteAllByUserId(userId);

        return orderMapper.toResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public Page<OrderSummaryResponse> getMyOrders(String userId, Pageable pageable) {
        return orderRepository.findAllByUserId(userId, pageable).map(orderMapper::toSummaryResponse);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderDetail(String userId, Long orderId) {
        Order order = orderRepository.findWithDetailsById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.ORDER_ACCESS_DENIED);
        }

        return orderMapper.toResponse(order);
    }

    public OrderResponse updateStatus(String userId, Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.ORDER_ACCESS_DENIED);
        }

        validateUserTransition(order.getStatus(), request.getStatus());

        if (request.getStatus() == OrderStatus.CANCELLED) {
            restoreStock(order);
        }

        order.setStatus(request.getStatus());
        return orderMapper.toResponse(orderRepository.save(order));
    }

    private void validateUserTransition(OrderStatus current, OrderStatus next) {
        if (!USER_CANCELLABLE.contains(current) || next != OrderStatus.CANCELLED) {
            throw new AppException(ErrorCode.INVALID_STATUS_TRANSITION);
        }
    }

    private void restoreStock(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            int updated = productRepository.incrementStock(item.getProduct().getId(), item.getQuantity());
            if (updated == 0) {
                throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
            }
        }
    }
}
