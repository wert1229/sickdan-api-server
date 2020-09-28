package com.kdpark.sickdan.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class SignDto {
    @Data
    public static class SignUpRequest {
        @NotBlank
        private String userId;
        @NotBlank
        private String password;
        @Email
        private String email;
        @NotBlank
        private String displayName;
    }

    @Data
    public static class SignInRequest {
        @NotBlank
        private String userId;
        @NotBlank
        private String password;
    }

    @Data
    public static class OAuthTokenInfo {
        @NotBlank
        private String accessToken;
        private String refreshToken;
        private long expiresAt;
        private String tokenType;
    }

    @Data
    public static class NaverUser {
        private String id;
        private String email;
        private String nickname;
        private String name;
    }

    @Data
    public static class KakaoUser {
        private String id;
        private String email;
        private String nickname;
    }

    @Data
    public static class RefreshRequest {
        @NotBlank
        private String refreshToken;
    }
}
