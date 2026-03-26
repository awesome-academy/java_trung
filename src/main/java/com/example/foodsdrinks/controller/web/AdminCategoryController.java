package com.example.foodsdrinks.controller.web;

import com.example.foodsdrinks.config.MessageHelper;
import com.example.foodsdrinks.dto.csv.importing.ImportCsvResult;
import com.example.foodsdrinks.dto.request.CategoryRequest;
import com.example.foodsdrinks.dto.response.CsvImportResponse;
import com.example.foodsdrinks.entity.Category;
import com.example.foodsdrinks.entity.enums.Classify;
import com.example.foodsdrinks.mapper.CategoryMapper;
import com.example.foodsdrinks.service.csv.CategoryCsvService;
import com.example.foodsdrinks.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;
    private final CategoryCsvService categoryCsvService;
    private final CategoryMapper categoryMapper;
    private final MessageHelper messageHelper;

    @ModelAttribute("classifyOptions")
    public Classify[] classifyOptions() {
        return Classify.values();
    }

    @GetMapping
    public String list(@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable, Model model) {
        Page<Category> categories = categoryService.getAllPaged(pageable);
        model.addAttribute("categories", categories);
        return "admin/categories/list";
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsv() {
        return categoryCsvService.exportCsv().toResponse(HttpStatus.OK);
    }

    @GetMapping("/import/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        return categoryCsvService.downloadTemplate().toResponse(HttpStatus.OK);
    }

    @PostMapping("/import")
    @ResponseBody
    public ResponseEntity<?> importCsv(@RequestParam("file") MultipartFile file) {
        return switch (categoryCsvService.importCsv(file)) {
            case ImportCsvResult.Success s   -> ResponseEntity.ok(CsvImportResponse.success(s.count()));
            case ImportCsvResult.RowErrors e -> e.errorCsv().toResponse(HttpStatus.UNPROCESSABLE_ENTITY);
            case ImportCsvResult.Rejected r  -> ResponseEntity.status(r.status()).body(CsvImportResponse.error(r.message()));
        };
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("categoryRequest", new CategoryRequest());
        return "admin/categories/create";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("categoryRequest") CategoryRequest categoryRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/categories/create";
        }
        categoryService.create(categoryRequest);
        redirectAttributes.addFlashAttribute("successMessage", messageHelper.get("success.admin.category.created"));
        return "redirect:/admin/categories";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Category category = categoryService.getCategoryEntityById(id);
        model.addAttribute("categoryRequest", categoryMapper.toRequest(category));
        model.addAttribute("categoryId", id);
        return "admin/categories/edit";
    }

    @PostMapping("/{id}/edit")
    public String edit(
            @PathVariable Long id,
            @Valid @ModelAttribute("categoryRequest") CategoryRequest categoryRequest,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categoryId", id);
            return "admin/categories/edit";
        }
        categoryService.update(id, categoryRequest);
        redirectAttributes.addFlashAttribute("successMessage", messageHelper.get("success.admin.category.updated"));
        return "redirect:/admin/categories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        categoryService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", messageHelper.get("success.admin.category.deleted"));
        return "redirect:/admin/categories";
    }
}
