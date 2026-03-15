package com.example.foodsdrinks.dto.request;

import com.example.foodsdrinks.entity.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserFilterRequest {

    private String email;
    private Role role;
    private Boolean active;
}
