package org.sopt.makers.api.common.security;

import static org.sopt.makers.api.common.exception.CommonFailureCode.INVALID_ADMIN_KEY;

import java.util.Objects;
import org.sopt.makers.api.common.exception.CommonException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AdminKeyValidator {

  @Value("${admin.key:}")
  private String adminKey;

  public void validate(String providedAdminKey) {
    if (!Objects.equals(adminKey, providedAdminKey)) {
      throw new CommonException(INVALID_ADMIN_KEY);
    }
  }
}
