package com.example.foodsdrinks.controller.web;

import com.example.foodsdrinks.config.MessageHelper;
import com.example.foodsdrinks.dto.request.CategoryRequest;
import com.example.foodsdrinks.entity.Category;
import com.example.foodsdrinks.entity.enums.Classify;
import com.example.foodsdrinks.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;
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
        CategoryRequest categoryRequest = new CategoryRequest();
        categoryRequest.setName(category.getName());
        categoryRequest.setClassify(category.getClassify());
        categoryRequest.setDescription(category.getDescription());
        model.addAttribute("categoryRequest", categoryRequest);
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
