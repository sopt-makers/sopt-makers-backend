package org.sopt.makers.domain.admin.auth.service;

import static org.sopt.makers.domain.admin.auth.exception.AdminAuthFailure.DUPLICATED_EMAIL;
import static org.sopt.makers.domain.admin.auth.exception.AdminAuthFailure.INVALID_EMAIL;
import static org.sopt.makers.domain.admin.auth.exception.AdminAuthFailure.INVALID_PASSWORD;
import static org.sopt.makers.domain.admin.auth.exception.AdminAuthFailure.NOT_FOUND_ADMIN;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.admin.auth.AdminAccount;
import org.sopt.makers.domain.admin.auth.AdminAccountType;
import org.sopt.makers.domain.admin.auth.exception.AdminAuthException;
import org.sopt.makers.domain.admin.auth.port.AdminAccountRepositoryPort;
import org.sopt.makers.domain.admin.auth.port.AdminRefreshTokenRepositoryPort;
import org.sopt.makers.domain.admin.auth.port.AdminTokenIssuerPort;
import org.sopt.makers.domain.admin.auth.port.AdminTokenIssuerPort.AdminTokenPair;
import org.sopt.makers.domain.admin.auth.port.PasswordHashPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminAuthService {

  private final AdminAccountRepositoryPort adminAccountRepositoryPort;
  private final AdminRefreshTokenRepositoryPort adminRefreshTokenRepositoryPort;
  private final AdminTokenIssuerPort adminTokenIssuerPort;
  private final PasswordHashPort passwordHashPort;

  @Transactional
  public AdminAccount signUp(
      String email, String rawPassword, String name, AdminAccountType accountType) {
    boolean isDuplicatedEmail = adminAccountRepositoryPort.existsByEmail(email);
    if (isDuplicatedEmail) {
      throw new AdminAuthException(DUPLICATED_EMAIL);
    }

    AdminAccount adminAccount =
        new AdminAccount(null, email, passwordHashPort.encode(rawPassword), name, accountType);
    return adminAccountRepositoryPort.save(adminAccount);
  }

  @Transactional
  public LoginResult login(String email, String rawPassword) {
    AdminAccount adminAccount = findByEmailOrThrow(email);
    boolean isInvalidPassword =
        !passwordHashPort.matches(rawPassword, adminAccount.encodedPassword());

    if (isInvalidPassword) {
      throw new AdminAuthException(INVALID_PASSWORD);
    }

    AdminTokenPair tokenPair = adminTokenIssuerPort.issue(adminAccount.id());
    return new LoginResult(adminAccount, tokenPair);
  }

  public record LoginResult(AdminAccount adminAccount, AdminTokenPair tokenPair) {}

  public AdminTokenPair refresh(String expiredAccessToken, String refreshToken) {
    return adminTokenIssuerPort.refresh(expiredAccessToken, refreshToken);
  }

  @Transactional
  public void changePassword(Long adminId, String oldPassword, String newPassword) {
    AdminAccount adminAccount = findByIdOrThrow(adminId);
    boolean isInvalidOldPassword =
        !passwordHashPort.matches(oldPassword, adminAccount.encodedPassword());

    if (isInvalidOldPassword) {
      throw new AdminAuthException(INVALID_PASSWORD);
    }

    AdminAccount updated =
        new AdminAccount(
            adminAccount.id(),
            adminAccount.email(),
            passwordHashPort.encode(newPassword),
            adminAccount.name(),
            adminAccount.accountType());
    adminAccountRepositoryPort.save(updated);
    adminRefreshTokenRepositoryPort.deleteAll(adminId);
  }

  private AdminAccount findByEmailOrThrow(String email) {
    return adminAccountRepositoryPort
        .findByEmail(email)
        .orElseThrow(() -> new AdminAuthException(INVALID_EMAIL));
  }

  private AdminAccount findByIdOrThrow(Long adminId) {
    return adminAccountRepositoryPort
        .findById(adminId)
        .orElseThrow(() -> new AdminAuthException(NOT_FOUND_ADMIN));
  }
}
