package com.example.foodsdrinks.service;

import com.example.foodsdrinks.dto.request.CreateRatingRequest;
import com.example.foodsdrinks.dto.response.RatingResponse;
import com.example.foodsdrinks.entity.Order;
import com.example.foodsdrinks.entity.OrderItem;
import com.example.foodsdrinks.entity.Product;
import com.example.foodsdrinks.entity.Rating;
import com.example.foodsdrinks.entity.User;
import com.example.foodsdrinks.entity.enums.OrderStatus;
import com.example.foodsdrinks.exception.AppException;
import com.example.foodsdrinks.exception.ErrorCode;
import com.example.foodsdrinks.mapper.RatingMapper;
import com.example.foodsdrinks.repository.OrderItemRepository;
import com.example.foodsdrinks.repository.ProductRepository;
import com.example.foodsdrinks.repository.RatingRepository;
import com.example.foodsdrinks.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Transactional
public class RatingService {

    private final RatingRepository ratingRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final RatingMapper ratingMapper;

    public RatingResponse createRating(String userId, CreateRatingRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));


        if (ratingRepository.existsByUserIdAndProductId(userId, request.getProductId())) {
            throw new AppException(ErrorCode.ALREADY_RATED);
        }

        OrderItem orderItem = orderItemRepository
                .findFirstByOrderUserIdAndProductIdAndOrderStatus(userId, request.getProductId(), OrderStatus.DONE)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_PURCHASED));
        Order order = orderItem.getOrder();

        User userRef = userRepository.getReferenceById(userId);
        Rating rating = Rating.builder()
                .user(userRef)
                .product(product)
                .order(order)
                .score(request.getScore())
                .comment(request.getComment())
                .build();
        Rating savedRating = ratingRepository.save(rating);

        BigDecimal avg = ratingRepository.findAvgScoreByProductId(request.getProductId())
                .map(BigDecimal::valueOf)
                .orElse(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
        int count = Math.toIntExact(ratingRepository.countByProductId(request.getProductId()));
        product.setAvgRating(avg);
        product.setRatingCount(count);

        return ratingMapper.toResponse(savedRating);
    }

    @Transactional(readOnly = true)
    public Page<RatingResponse> getRatingsByProductId(Long productId, Pageable pageable) {
        if (!productRepository.existsById(productId)) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        return ratingRepository.findAllByProductId(productId, pageable).map(ratingMapper::toResponse);
    }
}
