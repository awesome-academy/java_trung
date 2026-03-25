package com.example.foodsdrinks.controller.web;
import com.example.foodsdrinks.config.MessageHelper;
import com.example.foodsdrinks.dto.csv.CsvResponse;
import com.example.foodsdrinks.dto.csv.importing.ImportCsvResult;
import com.example.foodsdrinks.dto.request.CategoryRequest;
import com.example.foodsdrinks.entity.Category;
import com.example.foodsdrinks.entity.enums.Classify;
import com.example.foodsdrinks.exception.AppException;
import com.example.foodsdrinks.exception.ErrorCode;
import com.example.foodsdrinks.fixture.CategoryMother;
import com.example.foodsdrinks.mapper.CategoryMapper;
import com.example.foodsdrinks.repository.UserRepository;
import com.example.foodsdrinks.security.JwtUtil;
import com.example.foodsdrinks.service.csv.CategoryCsvService;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(AdminCategoryController.class)
@DisplayName("AdminCategoryController Unit Tests")
class AdminCategoryControllerTest {
    private final MockMvc mockMvc;
    @MockitoBean
    private CategoryService categoryService;
    @MockitoBean
    private CategoryCsvService categoryCsvService;
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
    class ListCategoriesTest {
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
        @DisplayName("list_whenUnauthenticated_redirectsToAdminLogin")
        void list_whenUnauthenticated_redirectsToAdminLogin() throws Exception {
            mockMvc.perform(get("/admin/categories"))
                    .andExpect(status().isUnauthorized());
        }
    }
    // ─────────────────────────────────────────────────
    // GET /admin/categories/export
    // ─────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /admin/categories/export")
    class ExportCsvTest {
        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("exportCsv_whenAdminAuthenticated_returnsCsvFile")
        void exportCsv_whenAdminAuthenticated_returnsCsvFile() throws Exception {
            byte[] csvBytes = "id,name,classify\n1,Coffee,DRINK\n".getBytes();
            given(categoryCsvService.exportCsv())
                    .willReturn(new CsvResponse("categories.csv", "text/csv; charset=UTF-8", csvBytes));
            mockMvc.perform(get("/admin/categories/export"))
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "text/csv; charset=UTF-8"))
                    .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"categories.csv\""))
                    .andExpect(content().bytes(csvBytes));
        }
    }
    // ─────────────────────────────────────────────────
    // GET /admin/categories/import/template
    // ─────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /admin/categories/import/template")
    class DownloadTemplateTest {
        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("downloadTemplate_whenAdminAuthenticated_returnsCsvTemplateFile")
        void downloadTemplate_whenAdminAuthenticated_returnsCsvTemplateFile() throws Exception {
            byte[] templateBytes = "id,name,classify,description\n".getBytes();
            given(categoryCsvService.downloadTemplate())
                    .willReturn(new CsvResponse("categories_template.csv", "text/csv; charset=UTF-8", templateBytes));
            mockMvc.perform(get("/admin/categories/import/template"))
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"categories_template.csv\""))
                    .andExpect(content().bytes(templateBytes));
        }
    }
    // ─────────────────────────────────────────────────
    // POST /admin/categories/import
    // ─────────────────────────────────────────────────
    @Nested
    @DisplayName("POST /admin/categories/import")
    class ImportCsvTest {
        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("importCsv_whenFileIsEmpty_returns400WithErrorMessage")
        void importCsv_whenFileIsEmpty_returns400WithErrorMessage() throws Exception {
            MockMultipartFile emptyFile = new MockMultipartFile(
                    "file", "test.csv", "text/csv", new byte[0]);
            given(categoryCsvService.importCsv(any()))
                    .willReturn(new ImportCsvResult.Rejected(HttpStatus.BAD_REQUEST, "Uploaded CSV file is empty"));

            mockMvc.perform(multipart("/admin/categories/import").file(emptyFile).with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.message").exists());
            then(categoryCsvService).should().importCsv(any());
        }
        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("importCsv_whenSuccessful_returns200WithSuccessTrueAndCount")
        void importCsv_whenSuccessful_returns200WithSuccessTrueAndCount() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "categories.csv", "text/csv", "data".getBytes());
            given(categoryCsvService.importCsv(any())).willReturn(new ImportCsvResult.Success(3));
            mockMvc.perform(multipart("/admin/categories/import").file(file).with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.count").value(3));
        }
        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("importCsv_whenValidationErrors_returns422WithErrorCsvBinary")
        void importCsv_whenValidationErrors_returns422WithErrorCsvBase64() throws Exception {
            MockMultipartFile file = new MockMultipartFile(
                    "file", "categories.csv", "text/csv", "data".getBytes());
            given(categoryCsvService.importCsv(any()))
                    .willReturn(new ImportCsvResult.RowErrors(
                            new CsvResponse("import_errors.csv", "text/csv; charset=UTF-8", "error-csv-content".getBytes())));
            mockMvc.perform(multipart("/admin/categories/import").file(file).with(csrf()))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(header().string("Content-Type", "text/csv; charset=UTF-8"))
                    .andExpect(header().string("Content-Disposition", "attachment; filename=\"import_errors.csv\""));
        }
    }
    // ─────────────────────────────────────────────────
    // GET /admin/categories/create
    // ─────────────────────────────────────────────────
    @Nested
    @DisplayName("GET /admin/categories/create")
    class CreateFormTest {
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
        @DisplayName("createForm_whenUnauthenticated_redirectsToAdminLogin")
        void createForm_whenUnauthenticated_redirectsToAdminLogin() throws Exception {
            mockMvc.perform(get("/admin/categories/create"))
                    .andExpect(status().isUnauthorized());
        }
    }
    // ─────────────────────────────────────────────────
    // POST /admin/categories
    // ─────────────────────────────────────────────────
    @Nested
    @DisplayName("POST /admin/categories")
    class CreateTest {
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
        @DisplayName("create_whenDuplicateName_redirectsToCategoryListWithErrorMessage")
        void create_whenDuplicateName_redirectsToCategoryListWithErrorMessage() throws Exception {
            given(categoryService.create(any(CategoryRequest.class)))
                    .willThrow(new AppException(ErrorCode.CATEGORY_ALREADY_EXISTS));
            given(messageHelper.get(ErrorCode.CATEGORY_ALREADY_EXISTS.getMessageKey()))
                    .willReturn("Category with this name and classify already exists");
            mockMvc.perform(post("/admin/categories")
                            .with(csrf())
                            .param("name", "Burger")
                            .param("classify", "FOOD"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin/categories"))
                    .andExpect(flash().attribute("errorMessage", "Category with this name and classify already exists"));
        }
        @Test
        @DisplayName("create_whenUnauthenticated_redirectsToAdminLogin")
        void create_whenUnauthenticated_redirectsToAdminLogin() throws Exception {
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
    class EditFormTest {
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
        @DisplayName("editForm_whenCategoryNotFound_redirectsToCategoryListWithErrorMessage")
        void editForm_whenCategoryNotFound_redirectsToCategoryListWithErrorMessage() throws Exception {
            given(categoryService.getCategoryEntityById(99L))
                    .willThrow(new AppException(ErrorCode.CATEGORY_NOT_FOUND));
            given(messageHelper.get(ErrorCode.CATEGORY_NOT_FOUND.getMessageKey()))
                    .willReturn("Category not found");
            mockMvc.perform(get("/admin/categories/{id}/edit", 99L))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin/categories"))
                    .andExpect(flash().attribute("errorMessage", "Category not found"));
        }
        @Test
        @DisplayName("editForm_whenUnauthenticated_redirectsToAdminLogin")
        void editForm_whenUnauthenticated_redirectsToAdminLogin() throws Exception {
            mockMvc.perform(get("/admin/categories/{id}/edit", 1L))
                    .andExpect(status().isUnauthorized());
        }
    }
    // ─────────────────────────────────────────────────
    // POST /admin/categories/{id}/edit
    // ─────────────────────────────────────────────────
    @Nested
    @DisplayName("POST /admin/categories/{id}/edit")
    class EditTest {
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
        @DisplayName("edit_whenDuplicateName_redirectsToCategoryListWithErrorMessage")
        void edit_whenDuplicateName_redirectsToCategoryListWithErrorMessage() throws Exception {
            given(categoryService.update(eq(1L), any(CategoryRequest.class)))
                    .willThrow(new AppException(ErrorCode.CATEGORY_ALREADY_EXISTS));
            given(messageHelper.get(ErrorCode.CATEGORY_ALREADY_EXISTS.getMessageKey()))
                    .willReturn("Category with this name and classify already exists");
            mockMvc.perform(post("/admin/categories/{id}/edit", 1L)
                            .with(csrf())
                            .param("name", "Pizza")
                            .param("classify", "FOOD"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin/categories"))
                    .andExpect(flash().attribute("errorMessage", "Category with this name and classify already exists"));
        }
        @Test
        @DisplayName("edit_whenUnauthenticated_redirectsToAdminLogin")
        void edit_whenUnauthenticated_redirectsToAdminLogin() throws Exception {
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
    class DeleteTest {
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
        @DisplayName("delete_whenUnauthenticated_redirectsToAdminLogin")
        void delete_whenUnauthenticated_redirectsToAdminLogin() throws Exception {
            mockMvc.perform(post("/admin/categories/{id}/delete", 1L)
                            .with(csrf()))
                    .andExpect(status().isUnauthorized());
        }
    }
}
