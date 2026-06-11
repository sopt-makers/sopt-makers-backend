package org.sopt.makers.domain.auth;

import static org.sopt.makers.domain.auth.exception.AuthFailure.INVALID_PHONE_VERIFICATION_TYPE;

import java.util.Arrays;
import org.sopt.makers.domain.auth.exception.AuthException;

public enum PhoneVerificationType {
  REGISTER,
  SEARCH_SOCIAL_PLATFORM,
  CHANGE_SOCIAL_PLATFORM,
  CHANGE_PHONE_NUMBER;

  public static PhoneVerificationType find(final String type) {
    return Arrays.stream(values())
        .filter(value -> value.name().equals(type))
        .findFirst()
        .orElseThrow(() -> new AuthException(INVALID_PHONE_VERIFICATION_TYPE));
  }
}
