package com.example.foodsdrinks.service;

import com.example.foodsdrinks.dto.request.ProductFilterRequest;
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
    public Page<ProductResponse> getAll(ProductFilterRequest filter, Pageable pageable) {
        if (filter.getMinPrice() != null && filter.getMaxPrice() != null
                && filter.getMinPrice().compareTo(filter.getMaxPrice()) > 0) {
            throw new AppException(ErrorCode.INVALID_PRICE_RANGE);
        }

        Specification<Product> spec = ProductSpecification.filter(
                filter.getSearch(),
                filter.getCategoryId(),
                filter.getClassify(),
                filter.getMinPrice(),
                filter.getMaxPrice(),
                filter.getAvailable());

        return productRepository.findAll(spec, pageable).map(productMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        return productMapper.toResponse(product);
    }
}
