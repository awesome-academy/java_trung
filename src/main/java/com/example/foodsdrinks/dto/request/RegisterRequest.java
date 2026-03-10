package com.example.foodsdrinks.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "{validation.email.required}")
    @Email(message = "{validation.email.invalid}")
    private String email;

    @NotBlank(message = "{validation.password.required}")
    @Size(min = 6, max = 100, message = "{validation.password.size}")
    private String password;

    @NotBlank(message = "{validation.full.name.required}")
    @Size(max = 100, message = "{validation.full.name.size}")
    private String fullName;

    @Size(max = 20, message = "{validation.phone.size}")
    private String phone;
}
