package com.example.foodsdrinks.controller.api;

import com.example.foodsdrinks.config.MessageHelper;
import com.example.foodsdrinks.dto.response.CategoryResponse;
import com.example.foodsdrinks.dto.response.ProductResponse;
import com.example.foodsdrinks.exception.AppException;
import com.example.foodsdrinks.exception.ErrorCode;
import com.example.foodsdrinks.fixture.CategoryMother;
import com.example.foodsdrinks.fixture.ProductMother;
import com.example.foodsdrinks.repository.UserRepository;
import com.example.foodsdrinks.security.JwtUtil;
import com.example.foodsdrinks.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@DisplayName("CategoryController Unit Tests")
class CategoryControllerTest {

    private final MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private MessageHelper messageHelper;

    CategoryControllerTest(@Autowired MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    // ─────────────────────────────────────────────────
    // GET /api/categories
    // ─────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/categories")
    class GetAll {

        @Test
        @WithMockUser
        @DisplayName("getAll_whenAuthenticated_returns200WithCategoryList")
        void getAll_whenAuthenticated_returns200WithCategoryList() throws Exception {
            CategoryResponse response = CategoryMother.defaultCategoryResponse();

            given(categoryService.getAll()).willReturn(List.of(response));

            mockMvc.perform(get("/api/categories")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data", hasSize(1)))
                    .andExpect(jsonPath("$.data[0].id").value(1))
                    .andExpect(jsonPath("$.data[0].name").value("Burger"))
                    .andExpect(jsonPath("$.data[0].classify").value("FOOD"));
        }

        @Test
        @WithMockUser
        @DisplayName("getAll_whenNoCategoriesExist_returns200WithEmptyList")
        void getAll_whenNoCategoriesExist_returns200WithEmptyList() throws Exception {
            given(categoryService.getAll()).willReturn(List.of());

            mockMvc.perform(get("/api/categories")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data", hasSize(0)));
        }

        @Test
        @DisplayName("getAll_whenUnauthenticated_returns401")
        void getAll_whenUnauthenticated_returns401() throws Exception {
            mockMvc.perform(get("/api/categories")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ─────────────────────────────────────────────────
    // GET /api/categories/{id}
    // ─────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/categories/{id}")
    class GetById {

        @Test
        @WithMockUser
        @DisplayName("getById_whenCategoryExists_returns200WithCategoryResponse")
        void getById_whenCategoryExists_returns200WithCategoryResponse() throws Exception {
            CategoryResponse response = CategoryMother.defaultCategoryResponse();

            given(categoryService.getById(1L)).willReturn(response);

            mockMvc.perform(get("/api/categories/{id}", 1L)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.name").value("Burger"))
                    .andExpect(jsonPath("$.data.classify").value("FOOD"));
        }

        @Test
        @WithMockUser
        @DisplayName("getById_whenCategoryNotFound_returns404")
        void getById_whenCategoryNotFound_returns404() throws Exception {
            given(messageHelper.get(ErrorCode.CATEGORY_NOT_FOUND.getMessageKey()))
                    .willReturn("Category not found");
            given(categoryService.getById(99L))
                    .willThrow(new AppException(ErrorCode.CATEGORY_NOT_FOUND));

            mockMvc.perform(get("/api/categories/{id}", 99L)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("getById_whenUnauthenticated_returns401")
        void getById_whenUnauthenticated_returns401() throws Exception {
            mockMvc.perform(get("/api/categories/{id}", 1L)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ─────────────────────────────────────────────────
    // GET /api/categories/{id}/products
    // ─────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /api/categories/{id}/products")
    class GetProductsByCategory {

        private final Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "avgRating"));

        @Test
        @WithMockUser
        @DisplayName("getProducts_whenCategoryExistsAndHasProducts_returns200WithPageResponse")
        void getProducts_whenCategoryExistsAndHasProducts_returns200WithPageResponse() throws Exception {
            ProductResponse productResponse = ProductMother.defaultProductResponse();
            given(categoryService.getProductsByCategoryId(eq(1L), eq(pageable)))
                    .willReturn(new PageImpl<>(List.of(productResponse), pageable, 1));

            mockMvc.perform(get("/api/categories/{id}/products", 1L)
                            .param("page", "0")
                            .param("size", "10")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.content", hasSize(1)))
                    .andExpect(jsonPath("$.data.content[0].id").value(10))
                    .andExpect(jsonPath("$.data.content[0].name").value("Cheese Burger"))
                    .andExpect(jsonPath("$.data.totalElements").value(1))
                    .andExpect(jsonPath("$.data.totalPages").value(1));
        }

        @Test
        @WithMockUser
        @DisplayName("getProducts_whenCategoryNotFound_returns404")
        void getProducts_whenCategoryNotFound_returns404() throws Exception {
            given(messageHelper.get(ErrorCode.CATEGORY_NOT_FOUND.getMessageKey()))
                    .willReturn("Category not found");
            given(categoryService.getProductsByCategoryId(eq(99L), any(Pageable.class)))
                    .willThrow(new AppException(ErrorCode.CATEGORY_NOT_FOUND));

            mockMvc.perform(get("/api/categories/{id}/products", 99L)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }

        @Test
        @DisplayName("getProducts_whenUnauthenticated_returns401")
        void getProducts_whenUnauthenticated_returns401() throws Exception {
            mockMvc.perform(get("/api/categories/{id}/products", 1L)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        @DisplayName("getProducts_whenCategoryExistsButNoProducts_returns200WithEmptyPage")
        void getProducts_whenCategoryExistsButNoProducts_returns200WithEmptyPage() throws Exception {
            given(categoryService.getProductsByCategoryId(eq(1L), eq(pageable)))
                    .willReturn(new PageImpl<>(List.of(), pageable, 0));

            mockMvc.perform(get("/api/categories/{id}/products", 1L)
                            .param("page", "0")
                            .param("size", "10")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content", hasSize(0)))
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }
    }
}
