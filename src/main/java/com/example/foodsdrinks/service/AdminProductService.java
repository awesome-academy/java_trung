package com.example.foodsdrinks.service;

import com.example.foodsdrinks.dto.request.ProductFilterRequest;
import com.example.foodsdrinks.dto.request.ProductRequest;
import com.example.foodsdrinks.entity.Category;
import com.example.foodsdrinks.entity.Product;
import com.example.foodsdrinks.exception.AppException;
import com.example.foodsdrinks.exception.ErrorCode;
import com.example.foodsdrinks.mapper.ProductMapper;
import com.example.foodsdrinks.repository.CategoryRepository;
import com.example.foodsdrinks.repository.OrderItemRepository;
import com.example.foodsdrinks.repository.ProductRepository;
import com.example.foodsdrinks.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminProductService {

    private static final String DEFAULT_IMAGE_URL = "https://example.com/default-product.png";

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final OrderItemRepository orderItemRepository;
    private final FileStorageService fileStorageService;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public Page<Product> getProducts(ProductFilterRequest filter, Pageable pageable) {
        if (filter.getMinPrice() != null && filter.getMaxPrice() != null
                && filter.getMinPrice().compareTo(filter.getMaxPrice()) > 0) {
            throw new AppException(ErrorCode.INVALID_PRICE_RANGE);
        }

        Specification<Product> specification = ProductSpecification.filter(
                filter.getSearch(),
                filter.getCategoryId(),
                filter.getClassify(),
                filter.getMinPrice(),
                filter.getMaxPrice(),
                filter.getAvailable());

        return productRepository.findAll(specification, pageable);
    }

    @Transactional(readOnly = true)
    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    public Product create(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        String imageUrl = resolveImageUrl(request.getImageFile(), null);

        Product product = productMapper.toEntity(request);
        product.setCategory(category);
        product.setImageUrl(imageUrl);
        product.setAvailable(request.getAvailable() == null || request.getAvailable());

        return productRepository.save(product);
    }

    public Product update(Long id, ProductRequest request) {
        Product product = getById(id);
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        String imageUrl = resolveImageUrl(request.getImageFile(), product.getImageUrl());

        productMapper.updateEntityFromRequest(request, product);
        product.setCategory(category);
        product.setImageUrl(imageUrl);
        product.setAvailable(request.getAvailable() != null && request.getAvailable());

        return productRepository.save(product);
    }

    public void delete(Long id) {
        Product product = getById(id);
        if (orderItemRepository.existsByProductId(id)) {
            throw new AppException(ErrorCode.PRODUCT_HAS_ORDERS);
        }

        if (isCustomImage(product.getImageUrl())) {
            fileStorageService.delete(product.getImageUrl());
        }

        productRepository.delete(product);
    }

    public void toggleAvailable(Long id) {
        Product product = getById(id);
        product.setAvailable(!product.isAvailable());
    }

    private String resolveImageUrl(MultipartFile imageFile, String currentImageUrl) {
        if (imageFile != null && !imageFile.isEmpty()) {
            if (isCustomImage(currentImageUrl)) {
                fileStorageService.delete(currentImageUrl);
            }
            return fileStorageService.store(imageFile);
        }

        if (currentImageUrl != null && !currentImageUrl.isBlank()) {
            return currentImageUrl;
        }

        return DEFAULT_IMAGE_URL;
    }

    private boolean isCustomImage(String imageUrl) {
        // Only treat locally uploaded product images as managed files eligible for deletion.
        return imageUrl != null
                && !imageUrl.isBlank()
                && !DEFAULT_IMAGE_URL.equals(imageUrl)
                && imageUrl.startsWith("/uploads/products/");
    }
}
