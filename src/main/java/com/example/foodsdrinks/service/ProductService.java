package com.example.foodsdrinks.service;

import com.example.foodsdrinks.dto.response.ProductResponse;
import com.example.foodsdrinks.entity.Product;
import com.example.foodsdrinks.entity.enums.Classify;
import com.example.foodsdrinks.exception.AppException;
import com.example.foodsdrinks.exception.ErrorCode;
import com.example.foodsdrinks.mapper.ProductMapper;
import com.example.foodsdrinks.repository.ProductRepository;
import com.example.foodsdrinks.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAll(
            String search,
            Long categoryId,
            Classify classify,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean available,
            Pageable pageable) {

        Specification<Product> spec = ProductSpecification.filter(
                search, categoryId, classify, minPrice, maxPrice, available);

        return productRepository.findAll(spec, pageable)
                .map(productMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return productMapper.toResponse(product);
    }
}
