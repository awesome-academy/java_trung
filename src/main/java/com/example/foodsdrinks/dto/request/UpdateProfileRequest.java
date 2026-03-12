package com.example.foodsdrinks.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequest {

    @Size(max = 100, message = "{validation.full.name.size}")
    private String fullName;

    @Size(max = 20, message = "{validation.phone.size}")
    private String phone;

    @Size(max = 500, message = "{validation.avatar.url.size}")
    private String avatarUrl;
}
