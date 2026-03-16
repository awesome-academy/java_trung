package com.example.foodsdrinks.security;

import com.example.foodsdrinks.entity.User;
import com.example.foodsdrinks.entity.enums.Role;
import com.example.foodsdrinks.exception.ErrorCode;
import com.example.foodsdrinks.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        ErrorCode.INVALID_CREDENTIALS.getMessageKey()));

        if (user.getRole() != Role.ADMIN) {
            throw new UsernameNotFoundException(ErrorCode.INVALID_CREDENTIALS.getMessageKey());
        }

        return new AdminUserPrincipal(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getPassword(),
                user.isActive(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
