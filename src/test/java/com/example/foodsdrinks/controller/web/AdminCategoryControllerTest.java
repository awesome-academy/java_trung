package com.example.foodsdrinks.controller.web;

import com.example.foodsdrinks.config.MessageHelper;
import com.example.foodsdrinks.dto.request.CategoryRequest;
import com.example.foodsdrinks.entity.Category;
import com.example.foodsdrinks.entity.enums.Classify;
import com.example.foodsdrinks.exception.AppException;
import com.example.foodsdrinks.exception.ErrorCode;
import com.example.foodsdrinks.fixture.CategoryMother;
import com.example.foodsdrinks.mapper.CategoryMapper;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminCategoryController.class)
@DisplayName("AdminCategoryController Unit Tests")
class AdminCategoryControllerTest {

    private final MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private CategoryMapper categoryMapper;

    @MockitoBean
    private MessageHelper messageHelper;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private UserDetailsService userDetailsService;

    AdminCategoryControllerTest(@Autowired MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    // ─────────────────────────────────────────────────
    // GET /admin/categories
    // ─────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /admin/categories")
    class List {

        private final Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("list_whenAdminAuthenticated_returns200AndContainsCategoriesInModel")
        void list_whenAdminAuthenticated_returns200AndContainsCategoriesInModel() throws Exception {
            Category category = CategoryMother.defaultCategory();
            var page = new PageImpl<>(java.util.List.of(category), pageable, 1);
            given(categoryService.getAllPaged(eq(pageable))).willReturn(page);

            mockMvc.perform(get("/admin/categories"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/categories/list"))
                    .andExpect(model().attribute("categories", page));
        }

        @Test
        @DisplayName("list_whenUnauthenticated_returns401")
        void list_whenUnauthenticated_returns401() throws Exception {
            mockMvc.perform(get("/admin/categories"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ─────────────────────────────────────────────────
    // GET /admin/categories/create
    // ─────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /admin/categories/create")
    class CreateForm {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("createForm_whenAdminAuthenticated_returns200WithEmptyCategoryRequest")
        void createForm_whenAdminAuthenticated_returns200WithEmptyCategoryRequest() throws Exception {
            mockMvc.perform(get("/admin/categories/create"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/categories/create"))
                    .andExpect(model().attributeExists("categoryRequest"));
        }

        @Test
        @DisplayName("createForm_whenUnauthenticated_returns401")
        void createForm_whenUnauthenticated_returns401() throws Exception {
            mockMvc.perform(get("/admin/categories/create"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ─────────────────────────────────────────────────
    // POST /admin/categories
    // ─────────────────────────────────────────────────
    @Nested
    @DisplayName("POST /admin/categories")
    class Create {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("create_whenValidRequest_redirectsToCategoryListWithSuccessMessage")
        void create_whenValidRequest_redirectsToCategoryListWithSuccessMessage() throws Exception {
            given(messageHelper.get("success.admin.category.created"))
                    .willReturn("Category created successfully.");

            mockMvc.perform(post("/admin/categories")
                            .with(csrf())
                            .param("name", "Burger")
                            .param("classify", "FOOD")
                            .param("description", "Burger category"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin/categories"))
                    .andExpect(flash().attribute("successMessage", "Category created successfully."));

            then(categoryService).should().create(any(CategoryRequest.class));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("create_whenNameIsBlank_returnsCreateFormWithValidationErrors")
        void create_whenNameIsBlank_returnsCreateFormWithValidationErrors() throws Exception {
            mockMvc.perform(post("/admin/categories")
                            .with(csrf())
                            .param("name", "")
                            .param("classify", "FOOD"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/categories/create"))
                    .andExpect(model().attributeHasFieldErrors("categoryRequest", "name"))
                    .andExpect(result ->
                            assertThat((Classify[]) result.getModelAndView().getModel().get("classifyOptions"))
                                    .containsExactly(Classify.values()));

            then(categoryService).should(never()).create(any());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("create_whenDuplicateName_propagatesAppException")
        void create_whenDuplicateName_propagatesAppException() throws Exception {
            given(categoryService.create(any(CategoryRequest.class)))
                    .willThrow(new AppException(ErrorCode.CATEGORY_ALREADY_EXISTS));

            mockMvc.perform(post("/admin/categories")
                            .with(csrf())
                            .param("name", "Burger")
                            .param("classify", "FOOD"))
                    .andExpect(result ->
                            assertThat(result.getResolvedException())
                                    .isInstanceOf(AppException.class)
                                    .extracting(ex -> ((AppException) ex).getErrorCode())
                                    .isEqualTo(ErrorCode.CATEGORY_ALREADY_EXISTS));
        }

        @Test
        @DisplayName("create_whenUnauthenticated_returns401")
        void create_whenUnauthenticated_returns401() throws Exception {
            mockMvc.perform(post("/admin/categories")
                            .with(csrf())
                            .param("name", "Burger")
                            .param("classify", "FOOD"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ─────────────────────────────────────────────────
    // GET /admin/categories/{id}/edit
    // ─────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /admin/categories/{id}/edit")
    class EditForm {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("editForm_whenCategoryExists_returns200WithCategoryRequestAndId")
        void editForm_whenCategoryExists_returns200WithCategoryRequestAndId() throws Exception {
            Category category = CategoryMother.defaultCategory();
            CategoryRequest categoryRequest = CategoryMother.createRequest();
            given(categoryService.getCategoryEntityById(1L)).willReturn(category);
            given(categoryMapper.toRequest(category)).willReturn(categoryRequest);

            mockMvc.perform(get("/admin/categories/{id}/edit", 1L))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/categories/edit"))
                    .andExpect(model().attribute("categoryRequest", categoryRequest))
                    .andExpect(model().attribute("categoryId", 1L));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("editForm_whenCategoryNotFound_propagatesAppException")
        void editForm_whenCategoryNotFound_propagatesAppException() throws Exception {
            given(categoryService.getCategoryEntityById(99L))
                    .willThrow(new AppException(ErrorCode.CATEGORY_NOT_FOUND));

            mockMvc.perform(get("/admin/categories/{id}/edit", 99L))
                    .andExpect(result ->
                            assertThat(result.getResolvedException())
                                    .isInstanceOf(AppException.class));
        }

        @Test
        @DisplayName("editForm_whenUnauthenticated_returns401")
        void editForm_whenUnauthenticated_returns401() throws Exception {
            mockMvc.perform(get("/admin/categories/{id}/edit", 1L))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ─────────────────────────────────────────────────
    // POST /admin/categories/{id}/edit
    // ─────────────────────────────────────────────────
    @Nested
    @DisplayName("POST /admin/categories/{id}/edit")
    class Edit {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("edit_whenValidRequest_redirectsToCategoryListWithSuccessMessage")
        void edit_whenValidRequest_redirectsToCategoryListWithSuccessMessage() throws Exception {
            given(messageHelper.get("success.admin.category.updated"))
                    .willReturn("Category updated successfully.");

            mockMvc.perform(post("/admin/categories/{id}/edit", 1L)
                            .with(csrf())
                            .param("name", "Pizza")
                            .param("classify", "FOOD")
                            .param("description", "Pizza category"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin/categories"))
                    .andExpect(flash().attribute("successMessage", "Category updated successfully."));

            then(categoryService).should().update(eq(1L), any(CategoryRequest.class));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("edit_whenNameIsBlank_returnsEditFormWithValidationErrorsAndCategoryId")
        void edit_whenNameIsBlank_returnsEditFormWithValidationErrorsAndCategoryId() throws Exception {
            mockMvc.perform(post("/admin/categories/{id}/edit", 1L)
                            .with(csrf())
                            .param("name", "")
                            .param("classify", "FOOD"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/categories/edit"))
                    .andExpect(model().attribute("categoryId", 1L))
                    .andExpect(model().attributeHasFieldErrors("categoryRequest", "name"))
                    .andExpect(result ->
                            assertThat((Classify[]) result.getModelAndView().getModel().get("classifyOptions"))
                                    .containsExactly(Classify.values()));

            then(categoryService).should(never()).update(any(), any());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("edit_whenDuplicateName_propagatesAppException")
        void edit_whenDuplicateName_propagatesAppException() throws Exception {
            given(categoryService.update(eq(1L), any(CategoryRequest.class)))
                    .willThrow(new AppException(ErrorCode.CATEGORY_ALREADY_EXISTS));

            mockMvc.perform(post("/admin/categories/{id}/edit", 1L)
                            .with(csrf())
                            .param("name", "Pizza")
                            .param("classify", "FOOD"))
                    .andExpect(result ->
                            assertThat(result.getResolvedException())
                                    .isInstanceOf(AppException.class)
                                    .extracting(ex -> ((AppException) ex).getErrorCode())
                                    .isEqualTo(ErrorCode.CATEGORY_ALREADY_EXISTS));
        }

        @Test
        @DisplayName("edit_whenUnauthenticated_returns401")
        void edit_whenUnauthenticated_returns401() throws Exception {
            mockMvc.perform(post("/admin/categories/{id}/edit", 1L)
                            .with(csrf())
                            .param("name", "Pizza")
                            .param("classify", "FOOD"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ─────────────────────────────────────────────────
    // POST /admin/categories/{id}/delete
    // ─────────────────────────────────────────────────
    @Nested
    @DisplayName("POST /admin/categories/{id}/delete")
    class Delete {

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("delete_whenCategoryExists_redirectsToCategoryListWithSuccessMessage")
        void delete_whenCategoryExists_redirectsToCategoryListWithSuccessMessage() throws Exception {
            given(messageHelper.get("success.admin.category.deleted"))
                    .willReturn("Category deleted successfully.");

            mockMvc.perform(post("/admin/categories/{id}/delete", 1L)
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin/categories"))
                    .andExpect(flash().attribute("successMessage", "Category deleted successfully."));

            then(categoryService).should().delete(1L);
        }

        @Test
        @DisplayName("delete_whenUnauthenticated_returns401")
        void delete_whenUnauthenticated_returns401() throws Exception {
            mockMvc.perform(post("/admin/categories/{id}/delete", 1L)
                            .with(csrf()))
                    .andExpect(status().isUnauthorized());
        }
    }
}
