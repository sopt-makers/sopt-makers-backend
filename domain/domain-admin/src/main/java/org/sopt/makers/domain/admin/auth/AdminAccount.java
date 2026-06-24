package org.sopt.makers.domain.admin.auth;

public record AdminAccount(
    Long id,
    String email,
    String encodedPassword,
    String name,
    AdminRole adminRole,
    AdminAccountStatus status) {

  public boolean isNotAllowed() {
    return this.status == AdminAccountStatus.NOT_CERTIFIED;
  }
}
