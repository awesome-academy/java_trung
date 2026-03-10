package com.example.foodsdrinks.dto.response;

import com.example.foodsdrinks.entity.enums.Role;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponse {

    private String id;
    private String email;
    private String fullName;
    private String phone;
    private String avatarUrl;
    private Role role;
    private boolean active;
    private LocalDateTime createdAt;
}
