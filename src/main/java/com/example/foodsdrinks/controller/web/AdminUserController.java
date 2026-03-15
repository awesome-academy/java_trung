package com.example.foodsdrinks.controller.web;

import com.example.foodsdrinks.config.MessageHelper;
import com.example.foodsdrinks.dto.request.UserFilterRequest;
import com.example.foodsdrinks.entity.User;
import com.example.foodsdrinks.entity.enums.Role;
import com.example.foodsdrinks.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;
    private final MessageHelper messageHelper;

    @ModelAttribute("roleOptions")
    public Role[] roleOptions() {
        return Role.values();
    }

    @GetMapping
    public String list(
            @ModelAttribute UserFilterRequest filter,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {
        Page<User> users = adminUserService.getUsers(filter, pageable);
        model.addAttribute("users", users);
        model.addAttribute("filter", filter);
        return "admin/users/list";
    }

    @PostMapping("/{id}/toggle-active")
    public String toggleActive(@PathVariable String id, RedirectAttributes redirectAttributes) {
        adminUserService.toggleActive(id);
        redirectAttributes.addFlashAttribute("successMessage", messageHelper.get("success.admin.user.status.updated"));
        return "redirect:/admin/users";
    }
}
