package org.sopt.makers.api.controller.auth.dto;

public record VerifyPhoneVerificationRequest(String phone, String code, String type) {}
