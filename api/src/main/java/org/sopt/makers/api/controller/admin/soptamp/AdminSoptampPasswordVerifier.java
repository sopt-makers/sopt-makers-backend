package org.sopt.makers.api.controller.admin.soptamp;

import org.sopt.makers.domain.app.soptamp.exception.SoptampException;
import org.sopt.makers.domain.app.soptamp.exception.SoptampFailure;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AdminSoptampPasswordVerifier {

  private final String adminPassword;

  public AdminSoptampPasswordVerifier(
      @Value("${sopt.soptamp.admin.password}") String adminPassword) {
    this.adminPassword = adminPassword;
  }

  public void verify(String password) {
    if (adminPassword == null || adminPassword.isBlank() || !adminPassword.equals(password)) {
      throw new SoptampException(SoptampFailure.INVALID_APP_ADMIN_PASSWORD);
    }
  }
}
