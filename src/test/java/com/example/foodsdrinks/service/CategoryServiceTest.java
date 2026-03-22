package com.example.foodsdrinks.service;

import com.example.foodsdrinks.dto.request.CategoryRequest;
import com.example.foodsdrinks.dto.response.CategoryResponse;
import com.example.foodsdrinks.dto.response.ProductResponse;
import com.example.foodsdrinks.entity.Category;
import com.example.foodsdrinks.entity.Product;
import com.example.foodsdrinks.entity.enums.Classify;
import com.example.foodsdrinks.exception.AppException;
import com.example.foodsdrinks.exception.ErrorCode;
import com.example.foodsdrinks.fixture.CategoryMother;
import com.example.foodsdrinks.fixture.ProductMother;
import com.example.foodsdrinks.mapper.CategoryMapper;
import com.example.foodsdrinks.mapper.ProductMapper;
import com.example.foodsdrinks.repository.CategoryRepository;
import com.example.foodsdrinks.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService Unit Tests")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryResponse categoryResponse;
    private CategoryRequest categoryRequest;

    @BeforeEach
    void setUp() {
        category         = CategoryMother.defaultCategory();
        categoryResponse = CategoryMother.defaultCategoryResponse();
        categoryRequest  = CategoryMother.createRequest();
    }

    // ─────────────────────────────────────────────────
    // getAll
    // ─────────────────────────────────────────────────
    @Nested
    @DisplayName("getAll")
    class GetAll {

        @Test
        @DisplayName("getAll_whenCategoriesExist_returnsListOfCategoryResponse")
        void getAll_whenCategoriesExist_returnsListOfCategoryResponse() {
            given(categoryRepository.findAll()).willReturn(List.of(category));
            given(categoryMapper.toResponse(category)).willReturn(categoryResponse);

            List<CategoryResponse> result = categoryService.getAll();

            assertThat(result).containsExactly(categoryResponse);
            then(categoryMapper).should().toResponse(category);
            then(categoryRepository).should().findAll();
        }

        @Test
        @DisplayName("getAll_whenNoCategoriesExist_returnsEmptyList")
        void getAll_whenNoCategoriesExist_returnsEmptyList() {
            given(categoryRepository.findAll()).willReturn(Collections.emptyList());

            List<CategoryResponse> result = categoryService.getAll();

            assertThat(result).isEmpty();
        }
    }

    // ─────────────────────────────────────────────────
    // getById
    // ─────────────────────────────────────────────────
    @Nested
    @DisplayName("getById")
    class GetById {

        @Test
        @DisplayName("getById_whenCategoryExists_returnsCategoryResponse")
        void getById_whenCategoryExists_returnsCategoryResponse() {
            given(categoryRepository.findById(1L)).willReturn(Optional.of(category));
            given(categoryMapper.toResponse(category)).willReturn(categoryResponse);

            CategoryResponse result = categoryService.getById(1L);

            assertThat(result).isEqualTo(categoryResponse);
            then(categoryMapper).should().toResponse(category);
        }

        @Test
        @DisplayName("getById_whenCategoryNotFound_throwsAppExceptionWithCategoryNotFound")
        void getById_whenCategoryNotFound_throwsAppExceptionWithCategoryNotFound() {
            given(categoryRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.getById(99L))
                    .isInstanceOf(AppException.class)
                    .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                            .isEqualTo(ErrorCode.CATEGORY_NOT_FOUND));
        }
    }

    // ─────────────────────────────────────────────────
    // getProductsByCategoryId
    // ─────────────────────────────────────────────────
    @Nested
    @DisplayName("getProductsByCategoryId")
    class GetProductsByCategoryId {

        @Test
        @DisplayName("getProductsByCategoryId_whenCategoryExistsAndHasProducts_returnsPageOfProductResponse")
        void getProductsByCategoryId_whenCategoryExistsAndHasProducts_returnsPageOfProductResponse() {
            Pageable pageable = PageRequest.of(0, 10);
            ProductResponse productResponse = ProductMother.defaultProductResponse();

            Page<Product> productPage = new PageImpl<>(List.of(new Product()), pageable, 1);
            given(categoryRepository.existsById(1L)).willReturn(true);
            given(productRepository.findAll(any(Specification.class), eq(pageable))).willReturn(productPage);
            given(productMapper.toResponse(any(Product.class))).willReturn(productResponse);

            Page<ProductResponse> result = categoryService.getProductsByCategoryId(1L, pageable);

            assertThat(result.getContent()).containsExactly(productResponse);
            assertThat(result.getTotalElements()).isEqualTo(1L);
            then(productMapper).should().toResponse(any(Product.class));
        }

        @Test
        @DisplayName("getProductsByCategoryId_whenCategoryNotFound_throwsAppExceptionWithCategoryNotFound")
        void getProductsByCategoryId_whenCategoryNotFound_throwsAppExceptionWithCategoryNotFound() {
            Pageable pageable = PageRequest.of(0, 10);
            given(categoryRepository.existsById(99L)).willReturn(false);

            assertThatThrownBy(() -> categoryService.getProductsByCategoryId(99L, pageable))
                    .isInstanceOf(AppException.class)
                    .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                            .isEqualTo(ErrorCode.CATEGORY_NOT_FOUND));

            then(productRepository).should(never()).findAll(any(Specification.class), any(Pageable.class));
        }
    }

    // ─────────────────────────────────────────────────
    // create
    // ─────────────────────────────────────────────────
    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("create_whenNameAndClassifyAreUnique_returnsSavedCategory")
        void create_whenNameAndClassifyAreUnique_returnsSavedCategory() {
            given(categoryRepository.existsByNameAndClassify("Burger", Classify.FOOD)).willReturn(false);
            given(categoryMapper.toEntity(categoryRequest)).willReturn(category);
            given(categoryRepository.save(category)).willReturn(category);

            Category result = categoryService.create(categoryRequest);

            assertThat(result).isEqualTo(category);
            then(categoryMapper).should().toEntity(categoryRequest);
            then(categoryRepository).should().save(category);
        }

        @Test
        @DisplayName("create_whenCategoryAlreadyExists_throwsAppExceptionWithCategoryAlreadyExists")
        void create_whenCategoryAlreadyExists_throwsAppExceptionWithCategoryAlreadyExists() {
            given(categoryRepository.existsByNameAndClassify("Burger", Classify.FOOD)).willReturn(true);

            assertThatThrownBy(() -> categoryService.create(categoryRequest))
                    .isInstanceOf(AppException.class)
                    .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                            .isEqualTo(ErrorCode.CATEGORY_ALREADY_EXISTS));

            then(categoryRepository).should(never()).save(any());
        }
    }

    // ─────────────────────────────────────────────────
    // update
    // ─────────────────────────────────────────────────
    @Nested
    @DisplayName("update")
    class Update {

        private CategoryRequest updateRequest;

        @BeforeEach
        void setUp() {
            updateRequest = CategoryMother.updateRequest();
        }

        @Test
        @DisplayName("update_whenCategoryExistsAndNameIsUnique_returnsUpdatedCategory")
        void update_whenCategoryExistsAndNameIsUnique_returnsUpdatedCategory() {
            given(categoryRepository.findById(1L)).willReturn(Optional.of(category));
            given(categoryRepository.existsByNameAndClassifyAndIdNot("Pizza", Classify.FOOD, 1L)).willReturn(false);
            given(categoryRepository.save(category)).willReturn(category);

            Category result = categoryService.update(1L, updateRequest);

            assertThat(result).isEqualTo(category);
            then(categoryMapper).should().updateEntity(updateRequest, category);
            then(categoryRepository).should().save(category);
        }

        @Test
        @DisplayName("update_whenCategoryNotFound_throwsAppExceptionWithCategoryNotFound")
        void update_whenCategoryNotFound_throwsAppExceptionWithCategoryNotFound() {
            given(categoryRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.update(99L, updateRequest))
                    .isInstanceOf(AppException.class)
                    .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                            .isEqualTo(ErrorCode.CATEGORY_NOT_FOUND));
        }

        @Test
        @DisplayName("update_whenDuplicateNameAndClassifyForAnotherCategory_throwsAppExceptionWithCategoryAlreadyExists")
        void update_whenDuplicateNameAndClassifyForAnotherCategory_throwsAppExceptionWithCategoryAlreadyExists() {
            given(categoryRepository.findById(1L)).willReturn(Optional.of(category));
            given(categoryRepository.existsByNameAndClassifyAndIdNot("Pizza", Classify.FOOD, 1L)).willReturn(true);

            assertThatThrownBy(() -> categoryService.update(1L, updateRequest))
                    .isInstanceOf(AppException.class)
                    .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                            .isEqualTo(ErrorCode.CATEGORY_ALREADY_EXISTS));

            then(categoryRepository).should(never()).save(any());
        }
    }

    // ─────────────────────────────────────────────────
    // delete
    // ─────────────────────────────────────────────────
    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("delete_whenCategoryExistsAndHasNoProducts_deleteSuccessfully")
        void delete_whenCategoryExistsAndHasNoProducts_deleteSuccessfully() {
            given(categoryRepository.findById(1L)).willReturn(Optional.of(category));

            categoryService.delete(1L);

            then(categoryRepository).should().delete(category);
        }

        @Test
        @DisplayName("delete_whenCategoryNotFound_throwsAppExceptionWithCategoryNotFound")
        void delete_whenCategoryNotFound_throwsAppExceptionWithCategoryNotFound() {
            given(categoryRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> categoryService.delete(99L))
                    .isInstanceOf(AppException.class)
                    .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                            .isEqualTo(ErrorCode.CATEGORY_NOT_FOUND));

            then(categoryRepository).should(never()).delete(any(Category.class));
        }

        @Test
        @DisplayName("delete_whenCategoryHasProducts_throwsAppExceptionWithCategoryHasProducts")
        void delete_whenCategoryHasProducts_throwsAppExceptionWithCategoryHasProducts() {
            Category categoryWithProducts = CategoryMother.categoryWithProducts();
            given(categoryRepository.findById(2L)).willReturn(Optional.of(categoryWithProducts));

            assertThatThrownBy(() -> categoryService.delete(2L))
                    .isInstanceOf(AppException.class)
                    .satisfies(ex -> assertThat(((AppException) ex).getErrorCode())
                            .isEqualTo(ErrorCode.CATEGORY_HAS_PRODUCTS));

            then(categoryRepository).should(never()).delete(any(Category.class));
        }
    }
}
