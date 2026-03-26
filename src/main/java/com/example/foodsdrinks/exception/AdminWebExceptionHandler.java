package com.example.foodsdrinks.exception;

import com.example.foodsdrinks.config.MessageHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.io.IOException;

@Slf4j
@ControllerAdvice(basePackages = "com.example.foodsdrinks.controller.web")
@RequiredArgsConstructor
public class AdminWebExceptionHandler {

    private static final String DEFAULT_ADMIN_REDIRECT = "redirect:/admin/dashboard";

    private final MessageHelper messageHelper;

    @ExceptionHandler(AppException.class)
    public Object handleAppException(AppException ex, HttpServletRequest request) {
        log.warn("Admin web AppException at {}: {}", request.getRequestURI(), ex.getMessage());
        if (isJsonRequest(request)) {
            return ResponseEntity
                    .status(ex.getErrorCode().getStatus())
                    .body(ErrorResponse.of(ex.getErrorCode(), messageHelper, ex.getArgs()));
        }
        addFlashError(request, resolveAppExceptionMessage(ex));
        return resolveRedirectPath(request);
    }

    @ExceptionHandler(IOException.class)
    public Object handleIOException(IOException ex, HttpServletRequest request) {
        log.error("I/O error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        if (isJsonRequest(request)) {
            return ResponseEntity
                    .status(ErrorCode.INTERNAL_ERROR.getStatus())
                    .body(ErrorResponse.of(ErrorCode.INTERNAL_ERROR, messageHelper));
        }
        addFlashError(request, messageHelper.get(ErrorCode.INTERNAL_ERROR.getMessageKey()));
        return resolveRedirectPath(request);
    }

    @ExceptionHandler(Exception.class)
    public Object handleGeneral(Exception ex, HttpServletRequest request) {
        log.error("Unhandled admin web exception at {}", request.getRequestURI(), ex);
        if (isJsonRequest(request)) {
            return ResponseEntity
                    .status(ErrorCode.INTERNAL_ERROR.getStatus())
                    .body(ErrorResponse.of(ErrorCode.INTERNAL_ERROR, messageHelper));
        }
        addFlashError(request, messageHelper.get(ErrorCode.INTERNAL_ERROR.getMessageKey()));
        return resolveRedirectPath(request);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private boolean isJsonRequest(HttpServletRequest request) {
        String xhr = request.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equalsIgnoreCase(xhr)) { return true; }
        String accept = request.getHeader("Accept");
        return accept != null && accept.contains(MediaType.APPLICATION_JSON_VALUE);
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
        if (uri.startsWith("/admin/users"))        return "redirect:/admin/users";
        if (uri.startsWith("/admin/categories"))   return "redirect:/admin/categories";
        if (uri.startsWith("/admin/products"))     return "redirect:/admin/products";
        if (uri.startsWith("/admin/orders"))       return "redirect:/admin/orders";
        if (uri.startsWith("/admin/suggestions"))  return "redirect:/admin/suggestions";
        if (uri.startsWith("/admin/login"))        return "redirect:/admin/login";
        if (uri.startsWith("/admin/dashboard"))    return "redirect:/admin/dashboard";
        return DEFAULT_ADMIN_REDIRECT;
    }
}
