package com.example.foodsdrinks.entity;

import com.example.foodsdrinks.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

	@Id
	@Column(length = 36, columnDefinition = "CHAR(36)")
	private String id;

	@Column(nullable = false, unique = true, length = 255)
	private String email;

	@Column(nullable = false, length = 255)
	private String password;

	@Column(name = "full_name", length = 100)
	private String fullName;

	@Column(length = 20)
	private String phone;

	@Column(name = "avatar_url", length = 500)
	@Builder.Default
	private String avatarUrl = "https://example.com/default-avatar.png";

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	@Builder.Default
	private Role role = Role.USER;

	@Column(name = "is_active", nullable = false)
	@Builder.Default
	private boolean active = true;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<CartItem> cartItems;

	@OneToMany(mappedBy = "user")
	private List<Order> orders;
}
