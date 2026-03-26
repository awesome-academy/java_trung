package com.example.foodsdrinks.provider;

import com.example.foodsdrinks.exception.AppException;
import com.example.foodsdrinks.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.Collections;

@Slf4j
@Component(SocialLoginProvider.GOOGLE_SERVICE)
public class GoogleLoginProvider implements SocialLoginProvider {

    // ── Fields ───────────────────────────────────────────────────────────────

    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final GoogleIdTokenVerifier verifier;
    private final RestClient restClient;

    // ── Constructor ──────────────────────────────────────────────────────────

    public GoogleLoginProvider(
            @Value("${app.google.client-id}")     String clientId,
            @Value("${app.google.client-secret}") String clientSecret,
            @Value("${app.google.redirect-uri}")  String redirectUri) {
        this.clientId     = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri  = redirectUri;
        this.verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(clientId))
                .build();
        this.restClient = buildRestClient();
    }

    // ── SocialLoginProvider ──────────────────────────────────────────────────

    @Override
    @CircuitBreaker(name = "googleTokenVerify", fallbackMethod = "verifyTokenFallback")
    public SocialProfile verifyToken(String authorizationCode) {
//        if (true) { throw new ResourceAccessException("Simulated timeout for testing circuit breaker"); }

        var idToken = exchangeCodeForIdToken(authorizationCode);

        try {
            var googleIdToken = verifier.verify(idToken);
            if (googleIdToken == null) {
                log.warn("Google ID token verification returned null – token may be invalid or expired");
                throw new AppException(ErrorCode.INVALID_SOCIAL_TOKEN);
            }
            return buildProfile(googleIdToken.getPayload());
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during Google auth code flow: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.INVALID_SOCIAL_TOKEN);
        }
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private String exchangeCodeForIdToken(String code) {
        var form = new LinkedMultiValueMap<String, String>();
        form.add("code",          code);
        form.add("client_id",     clientId);
        form.add("client_secret", clientSecret);
        form.add("redirect_uri",  redirectUri);
        form.add("grant_type",    "authorization_code");

        var response = restClient.post()
                .uri("https://oauth2.googleapis.com/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(TokenExchangeResponse.class);

        if (response == null || response.idToken() == null) {
            log.warn("Token exchange with Google returned no id_token");
            throw new AppException(ErrorCode.INVALID_SOCIAL_TOKEN);
        }

        return response.idToken();
    }

    private SocialProfile buildProfile(Payload payload) {
        var email   = payload.getEmail();
        var sub     = payload.getSubject();
        var name    = (String) payload.get("name");
        var picture = (String) payload.get("picture");
        log.debug("Google token verified – sub={}, email={}", sub, email);
        return new SocialProfile(email, sub, name, picture);
    }

    private SocialProfile verifyTokenFallback(String authorizationCode, Throwable cause) {
        if (cause instanceof CallNotPermittedException) {
            log.warn("Google auth circuit is OPEN – fast-fail without calling Google: {}", cause.getMessage());
        } else {
            log.error("Google auth timed out, circuit breaker recorded a failure: {}", cause.getMessage(), cause);
        }
        throw new AppException(ErrorCode.GOOGLE_SERVICE_UNAVAILABLE);
    }

    private static RestClient buildRestClient() {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(3));
        factory.setReadTimeout(Duration.ofSeconds(5));
        return RestClient.builder().requestFactory(factory).build();
    }

    private record TokenExchangeResponse(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("id_token")     String idToken,
            @JsonProperty("token_type")   String tokenType,
            @JsonProperty("expires_in")   int    expiresIn,
            @JsonProperty("scope")        String scope
    ) {}
}
