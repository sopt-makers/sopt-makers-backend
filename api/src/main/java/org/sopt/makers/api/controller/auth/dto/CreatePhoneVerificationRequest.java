package org.sopt.makers.api.controller.auth.dto;

public record CreatePhoneVerificationRequest(Long userId, String phone, String type) {}
