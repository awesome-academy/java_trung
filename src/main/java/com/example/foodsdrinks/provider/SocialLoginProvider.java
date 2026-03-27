package com.example.foodsdrinks.provider;

public interface SocialLoginProvider {
    String GOOGLE_SERVICE = "GOOGLE";

    SocialProfile verifyToken(String authorizationCode);
}
