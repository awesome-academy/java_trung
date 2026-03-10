package com.example.foodsdrinks.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "error.email.already.exists"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "error.invalid.credentials"),
    ACCOUNT_DISABLED(HttpStatus.FORBIDDEN, "error.account.disabled"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "error.unauthorized"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "error.forbidden"),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "error.user.not.found"),

    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "error.category.not.found"),
    CATEGORY_ALREADY_EXISTS(HttpStatus.CONFLICT, "error.category.already.exists"),
    CATEGORY_HAS_PRODUCTS(HttpStatus.CONFLICT, "error.category.has.products"),

    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "error.product.not.found"),
    PRODUCT_UNAVAILABLE(HttpStatus.BAD_REQUEST, "error.product.unavailable"),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "error.insufficient.stock"),

    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "error.cart.item.not.found"),
    CART_EMPTY(HttpStatus.BAD_REQUEST, "error.cart.empty"),

    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "error.order.not.found"),
    ORDER_ACCESS_DENIED(HttpStatus.FORBIDDEN, "error.order.access.denied"),
    INVALID_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "error.invalid.status.transition"),

    RATING_NOT_FOUND(HttpStatus.NOT_FOUND, "error.rating.not.found"),
    ALREADY_RATED(HttpStatus.CONFLICT, "error.already.rated"),
    NOT_PURCHASED(HttpStatus.FORBIDDEN, "error.not.purchased"),

    SUGGESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "error.suggestion.not.found"),

    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "error.validation.failed"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "error.internal"),

    MALFORMED_JSON(HttpStatus.BAD_REQUEST, "error.malformed.json"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "error.method.not.allowed");

    private final HttpStatus status;
    private final String messageKey;

    public String formatMessage(Object... args) {
        return String.format(this.messageKey, args);
    }
}
