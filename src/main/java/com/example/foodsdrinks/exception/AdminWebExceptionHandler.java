package com.example.foodsdrinks.exception;

import com.example.foodsdrinks.config.MessageHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.RequestContextUtils;

@Slf4j
@ControllerAdvice(basePackages = "com.example.foodsdrinks.controller.web", annotations = Controller.class)
@RequiredArgsConstructor
public class AdminWebExceptionHandler {

    private static final String DEFAULT_ADMIN_REDIRECT = "redirect:/admin/dashboard";

    private final MessageHelper messageHelper;

    @ExceptionHandler(AppException.class)
    public String handleAppException(AppException ex, HttpServletRequest request) {
        log.warn("Admin web AppException at {}: {}", request.getRequestURI(), ex.getMessage());
        addFlashError(request, resolveAppExceptionMessage(ex));
        return resolveRedirectPath(request);
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneral(Exception ex, HttpServletRequest request) {
        log.error("Unhandled admin web exception at {}", request.getRequestURI(), ex);
        addFlashError(request, messageHelper.get(ErrorCode.INTERNAL_ERROR.getMessageKey()));
        return resolveRedirectPath(request);
    }

    private String resolveAppExceptionMessage(AppException exception) {
        Object[] args = exception.getArgs();
        return args == null
                ? messageHelper.get(exception.getErrorCode().getMessageKey())
                : messageHelper.get(exception.getErrorCode().getMessageKey(), args);
    }

    private void addFlashError(HttpServletRequest request, String message) {
        FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
        flashMap.put("errorMessage", message);
    }

    private String resolveRedirectPath(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri == null || uri.isBlank()) {
            return DEFAULT_ADMIN_REDIRECT;
        }
        if (uri.startsWith("/admin/users")) {
            return "redirect:/admin/users";
        }
        if (uri.startsWith("/admin/categories")) {
            return "redirect:/admin/categories";
        }
        if (uri.startsWith("/admin/login")) {
            return "redirect:/admin/login";
        }
        if (uri.startsWith("/admin/dashboard")) {
            return "redirect:/admin/dashboard";
        }
        return DEFAULT_ADMIN_REDIRECT;
    }
}
