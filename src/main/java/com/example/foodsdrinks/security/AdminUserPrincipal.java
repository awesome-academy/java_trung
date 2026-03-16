package com.example.foodsdrinks.security;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(of = "id")
public class AdminUserPrincipal implements UserDetails, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String email;
    private final String fullName;
    private final String password;
    private final boolean active;
    private final Collection<? extends GrantedAuthority> authorities;

    public String getDisplayName() {
        return fullName != null && !fullName.isBlank() ? fullName : email;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
