package org.sopt.makers.api.controller.admin.auth.dto;

import jakarta.validation.constraints.NotBlank;

public sealed interface AdminAuthRequest {

  record AdminLogin(@NotBlank String email, @NotBlank String password)
      implements AdminAuthRequest {}

  record ChangePassword(@NotBlank String oldPassword, @NotBlank String newPassword)
      implements AdminAuthRequest {}
}
