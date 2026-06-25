package org.sopt.makers.api.controller.admin.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.sopt.makers.domain.admin.auth.AdminAccountType;

public sealed interface AdminAuthRequest {

  record AdminSignUp(
      @NotBlank String email,
      @NotBlank String password,
      @NotBlank String name,
      @NotNull AdminAccountType accountType)
      implements AdminAuthRequest {}

  record AdminLogin(@NotBlank String email, @NotBlank String password)
      implements AdminAuthRequest {}

  record ChangePassword(@NotBlank String oldPassword, @NotBlank String newPassword)
      implements AdminAuthRequest {}
}
