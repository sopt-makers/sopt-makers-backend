package org.sopt.makers.api.controller.admin.auth.dto;

import org.sopt.makers.domain.admin.auth.AdminRole;

public sealed interface AdminAuthRequest {

  record SignUp(String email, String password, String name, AdminRole adminRole)
      implements AdminAuthRequest {}

  record Login(String email, String password) implements AdminAuthRequest {}

  record ChangePassword(String oldPassword, String newPassword) implements AdminAuthRequest {}
}
