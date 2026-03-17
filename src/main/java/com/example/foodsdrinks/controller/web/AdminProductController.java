package com.example.foodsdrinks.controller.web;

import com.example.foodsdrinks.config.MessageHelper;
import com.example.foodsdrinks.dto.request.ProductFilterRequest;
import com.example.foodsdrinks.dto.request.ProductRequest;
import com.example.foodsdrinks.entity.Category;
import com.example.foodsdrinks.entity.Product;
import com.example.foodsdrinks.entity.enums.Classify;
import com.example.foodsdrinks.mapper.ProductMapper;
import com.example.foodsdrinks.repository.CategoryRepository;
import com.example.foodsdrinks.service.AdminProductService;
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

import java.util.List;

@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final AdminProductService adminProductService;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final MessageHelper messageHelper;

    @ModelAttribute("classifyOptions")
    public Classify[] classifyOptions() {
        return Classify.values();
    }

    @ModelAttribute("categories")
    public List<Category> categories() {
        return categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    @GetMapping
    public String list(
            @ModelAttribute("filter") ProductFilterRequest filter,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {
        Page<Product> products = adminProductService.getProducts(filter, pageable);
        model.addAttribute("products", products);
        return "admin/products/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("productRequest", new ProductRequest());
        return "admin/products/create";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("productRequest") ProductRequest productRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "admin/products/create";
        }

        adminProductService.create(productRequest);
        redirectAttributes.addFlashAttribute("successMessage", messageHelper.get("success.admin.product.created"));
        return "redirect:/admin/products";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Product product = adminProductService.getById(id);
        ProductRequest productRequest = productMapper.toRequest(product);

        model.addAttribute("productRequest", productRequest);
        model.addAttribute("productId", id);
        model.addAttribute("currentImageUrl", product.getImageUrl());
        return "admin/products/edit";
    }

    @PostMapping("/{id}/edit")
    public String edit(
            @PathVariable Long id,
            @Valid @ModelAttribute("productRequest") ProductRequest productRequest,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("productId", id);
            Product currentProduct = adminProductService.getById(id);
            model.addAttribute("currentImageUrl", currentProduct.getImageUrl());
            return "admin/products/edit";
        }

        adminProductService.update(id, productRequest);
        redirectAttributes.addFlashAttribute("successMessage", messageHelper.get("success.admin.product.updated"));
        return "redirect:/admin/products";
    }

    @PostMapping("/{id}/toggle-available")
    public String toggleAvailable(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        adminProductService.toggleAvailable(id);
        redirectAttributes.addFlashAttribute("successMessage", messageHelper.get("success.admin.product.status.updated"));
        return "redirect:/admin/products";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        adminProductService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", messageHelper.get("success.admin.product.deleted"));
        return "redirect:/admin/products";
    }
}
