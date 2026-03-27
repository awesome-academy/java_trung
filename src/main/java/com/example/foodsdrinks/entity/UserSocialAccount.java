package com.example.foodsdrinks.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "user_social_accounts",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_social_provider_id", columnNames = {"provider_name", "provider_user_id"}),
        @UniqueConstraint(name = "uq_user_provider",      columnNames = {"user_id",       "provider_name"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSocialAccount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "provider_name", nullable = false, length = 30)
    private String providerName;

    @Column(name = "provider_user_id", nullable = false, length = 255)
    private String providerUserId;
}
