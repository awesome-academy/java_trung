package com.example.foodsdrinks.repository;

import com.example.foodsdrinks.entity.UserSocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSocialAccountRepository extends JpaRepository<UserSocialAccount, Long> {

    Optional<UserSocialAccount> findByProviderNameAndProviderUserId(String providerName, String providerUserId);

    Optional<UserSocialAccount> findByUserIdAndProviderName(String userId, String providerName);

    boolean existsByProviderNameAndProviderUserIdAndUserIdNot(
            String providerName, String providerUserId, String excludedUserId);
}
