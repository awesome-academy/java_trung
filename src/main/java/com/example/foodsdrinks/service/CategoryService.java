package com.example.foodsdrinks.service;

import com.example.foodsdrinks.dto.request.CategoryRequest;
import com.example.foodsdrinks.dto.response.CategoryResponse;
import com.example.foodsdrinks.dto.response.ProductResponse;
import com.example.foodsdrinks.entity.Category;
import com.example.foodsdrinks.entity.Product;
import com.example.foodsdrinks.exception.AppException;
import com.example.foodsdrinks.exception.ErrorCode;
import com.example.foodsdrinks.mapper.CategoryMapper;
import com.example.foodsdrinks.mapper.ProductMapper;
import com.example.foodsdrinks.repository.CategoryRepository;
import com.example.foodsdrinks.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream().map(categoryMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        return categoryMapper.toResponse(category);
    }

    @Transactional(readOnly = true)
    public Category getCategoryEntityById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Page<Category> getAllPaged(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByCategoryId(Long categoryId, Pageable pageable) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        Specification<Product> spec = (root, query, cb) ->
                cb.equal(root.get("category").get("id"), categoryId);
        return productRepository.findAll(spec, pageable).map(productMapper::toResponse);
    }

    @Transactional
    public Category create(CategoryRequest request) {
        if (categoryRepository.existsByNameAndClassify(request.getName(), request.getClassify())) {
            throw new AppException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }
        Category category = Category.builder()
                .name(request.getName())
                .classify(request.getClassify())
                .description(request.getDescription())
                .build();
        return categoryRepository.save(category);
    }

    @Transactional
    public Category update(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        if (categoryRepository.existsByNameAndClassifyAndIdNot(request.getName(), request.getClassify(), id)) {
            throw new AppException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }
        category.setName(request.getName());
        category.setClassify(request.getClassify());
        category.setDescription(request.getDescription());
        return categoryRepository.save(category);
    }

    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
        if (category.getProducts() != null && !category.getProducts().isEmpty()) {
            throw new AppException(ErrorCode.CATEGORY_HAS_PRODUCTS);
        }
        categoryRepository.delete(category);
    }
}
