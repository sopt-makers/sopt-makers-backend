package org.sopt.makers.domain.auth.adapter;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.auth.PhoneVerificationType;
import org.sopt.makers.domain.auth.service.AuthService;
import org.sopt.makers.domain.user.port.PhoneVerificationValidatorPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PhoneVerificationValidatorAdapter implements PhoneVerificationValidatorPort {

  private final AuthService authService;

  @Override
  public void validateChangePhoneNumberVerified(String phone) {
    authService.validateVerified(phone, PhoneVerificationType.CHANGE_PHONE_NUMBER);
  }
}
