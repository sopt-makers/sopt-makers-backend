package org.sopt.makers.api.controller.auth.dto;

public record TokenRefreshForAppRequest(String accessToken, String refreshToken) {}
