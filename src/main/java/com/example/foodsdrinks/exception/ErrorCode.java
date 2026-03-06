package com.example.foodsdrinks.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "Email already exists"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid email or password"),
    ACCOUNT_DISABLED(HttpStatus.FORBIDDEN, "Account is disabled"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Authentication required"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Access denied"),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),

    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "Category not found"),
    CATEGORY_ALREADY_EXISTS(HttpStatus.CONFLICT, "Category with this name and classify already exists"),
    CATEGORY_HAS_PRODUCTS(HttpStatus.CONFLICT, "Cannot delete category that has products"),

    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "Product not found"),
    PRODUCT_UNAVAILABLE(HttpStatus.BAD_REQUEST, "Product is not available"),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "Insufficient stock for product: %s"),

    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "Cart item not found"),
    CART_EMPTY(HttpStatus.BAD_REQUEST, "Cart is empty"),

    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "Order not found"),
    ORDER_ACCESS_DENIED(HttpStatus.FORBIDDEN, "You do not have access to this order"),
    INVALID_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "Invalid order status transition"),

    RATING_NOT_FOUND(HttpStatus.NOT_FOUND, "Rating not found"),
    ALREADY_RATED(HttpStatus.CONFLICT, "You have already rated this product"),
    NOT_PURCHASED(HttpStatus.FORBIDDEN, "You can only rate products you have purchased"),

    SUGGESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Suggestion not found"),

    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "Validation failed"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");

    private final HttpStatus status;
    private final String message;

    public String formatMessage(Object... args) {
        return String.format(this.message, args);
    }
}
