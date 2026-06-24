package org.sopt.makers.api.controller.admin.auth.dto;

import org.sopt.makers.domain.admin.auth.AdminAccount;
import org.sopt.makers.domain.admin.auth.AdminAccountStatus;
import org.sopt.makers.domain.admin.auth.AdminRole;
import org.sopt.makers.domain.admin.auth.port.AdminTokenIssuerPort.AdminTokenPair;
import org.sopt.makers.domain.admin.auth.service.AdminAuthService.LoginResult;

public sealed interface AdminAuthResponse {

  record SignUpResult(Long id, String email, String name, AdminRole adminRole)
      implements AdminAuthResponse {

    public static SignUpResult from(AdminAccount adminAccount) {
      return new SignUpResult(
          adminAccount.id(), adminAccount.email(), adminAccount.name(), adminAccount.adminRole());
    }
  }

  record LoginBody(Long id, String name, AdminAccountStatus adminAccountStatus, String accessToken)
      implements AdminAuthResponse {

    public static LoginBody of(LoginResult loginResult) {
      AdminAccount adminAccount = loginResult.adminAccount();
      return new LoginBody(
          adminAccount.id(),
          adminAccount.name(),
          adminAccount.status(),
          loginResult.tokenPair().accessToken());
    }
  }

  record RefreshResult(String accessToken) implements AdminAuthResponse {

    public static RefreshResult from(AdminTokenPair tokenPair) {
      return new RefreshResult(tokenPair.accessToken());
    }
  }
}
