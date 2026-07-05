package org.sopt.makers.api.controller.auth.dto;

public record UpdateSocialAccountRequest(String phone, String token, String authPlatform) {}
