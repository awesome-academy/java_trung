package com.example.foodsdrinks.dto.request;

import com.example.foodsdrinks.entity.enums.Classify;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequest {

    @NotBlank(message = "{validation.product.name.required}")
    @Size(max = 200, message = "{validation.product.name.size}")
    private String name;

    @Size(max = 1000, message = "{validation.product.description.size}")
    private String description;

    @NotNull(message = "{validation.product.price.required}")
    @DecimalMin(value = "0.0", message = "{validation.product.price.min}")
    @Digits(integer = 10, fraction = 2, message = "{validation.product.price.digits}")
    private BigDecimal price;

    @NotNull(message = "{validation.product.stock.required}")
    @Min(value = 0, message = "{validation.product.stock.min}")
    private Integer stock;

    @NotNull(message = "{validation.product.category.required}")
    private Long categoryId;

    @NotNull(message = "{validation.product.classify.required}")
    private Classify classify;

    private Boolean available;

    private MultipartFile imageFile;
}
