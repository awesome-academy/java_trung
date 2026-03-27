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

    // Social login
    EMAIL_CONFLICT_LOCAL_ACCOUNT(HttpStatus.CONFLICT, "error.social.email.conflict.local"),
    SOCIAL_ACCOUNT_ALREADY_LINKED(HttpStatus.CONFLICT, "error.social.account.already.linked"),
    UNSUPPORTED_PROVIDER(HttpStatus.BAD_REQUEST, "error.social.unsupported.provider"),
    INVALID_SOCIAL_TOKEN(HttpStatus.UNAUTHORIZED, "error.social.invalid.token"),
    INVALID_OLD_PASSWORD(HttpStatus.BAD_REQUEST, "error.social.invalid.old.password"),
    SOCIAL_ONLY_ACCOUNT(HttpStatus.BAD_REQUEST, "error.social.only.account"),
    GOOGLE_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "error.social.google.unavailable"),

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "error.user.not.found"),
    CURRENT_USER_CANNOT_BE_DISABLED(HttpStatus.BAD_REQUEST, "error.current.user.cannot.be.disabled"),

    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "error.category.not.found"),
    CATEGORY_ALREADY_EXISTS(HttpStatus.CONFLICT, "error.category.already.exists"),
    CATEGORY_HAS_PRODUCTS(HttpStatus.CONFLICT, "error.category.has.products"),

    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "error.product.not.found"),
    PRODUCT_UNAVAILABLE(HttpStatus.BAD_REQUEST, "error.product.unavailable"),
    INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "error.insufficient.stock"),
    PRODUCT_HAS_ORDERS(HttpStatus.CONFLICT, "error.product.has.orders"),

    CART_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "error.cart.item.not.found"),
    CART_EMPTY(HttpStatus.BAD_REQUEST, "error.cart.empty"),

    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "error.order.not.found"),
    ORDER_ACCESS_DENIED(HttpStatus.FORBIDDEN, "error.order.access.denied"),
    INVALID_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "error.invalid.status.transition"),

    RATING_NOT_FOUND(HttpStatus.NOT_FOUND, "error.rating.not.found"),
    ALREADY_RATED(HttpStatus.CONFLICT, "error.already.rated"),
    NOT_PURCHASED(HttpStatus.FORBIDDEN, "error.not.purchased"),

    SUGGESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "error.suggestion.not.found"),
    SUGGESTION_INVALID_STATUS_TRANSITION(HttpStatus.CONFLICT, "error.suggestion.invalid.status.transition"),

    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "error.validation.failed"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "error.internal"),

    MALFORMED_JSON(HttpStatus.BAD_REQUEST, "error.malformed.json"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "error.method.not.allowed"),

    INVALID_PRICE_RANGE(HttpStatus.BAD_REQUEST, "error.invalid.price.range"),
    INVALID_ENUM_VALUE(HttpStatus.BAD_REQUEST, "error.invalid.enum.value"),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "error.invalid.file.type"),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "error.file.upload.failed"),

    RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "error.rate.limit.exceeded"),
    CSV_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "error.csv.invalid.format"),
    CSV_EMPTY(HttpStatus.BAD_REQUEST, "error.csv.empty"),
    CSV_FILE_TOO_LARGE(HttpStatus.BAD_REQUEST, "error.csv.file.too.large");

    private final HttpStatus status;
    private final String messageKey;

    public String formatMessage(Object... args) {
        return String.format(this.messageKey, args);
    }
}
