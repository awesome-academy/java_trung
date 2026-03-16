package com.example.foodsdrinks.controller.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.example.foodsdrinks.security.AdminUserPrincipal;

@ControllerAdvice(basePackages = "com.example.foodsdrinks.controller.web")
public class AdminWebModelAdvice {

    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute("currentAdmin")
    public AdminUserPrincipal currentAdmin(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof AdminUserPrincipal principal) {
            return principal;
        }
        return null;
    }
}
