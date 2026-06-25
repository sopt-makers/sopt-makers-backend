package org.sopt.makers.domain.admin.auth;

public record AdminAccount(
    Long id, String email, String encodedPassword, String name, AdminAccountType accountType) {}
