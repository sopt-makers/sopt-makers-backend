package org.sopt.makers.domain.auth.service;

import static org.sopt.makers.domain.auth.exception.AuthFailure.EXPIRED_PHONE_VERIFICATION;
import static org.sopt.makers.domain.auth.exception.AuthFailure.INVALID_PHONE_VERIFICATION_CODE;
import static org.sopt.makers.domain.auth.exception.AuthFailure.NOT_FOUND_PHONE_VERIFICATION;
import static org.sopt.makers.domain.auth.exception.AuthFailure.PHONE_NOT_VERIFIED;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.auth.PhoneVerification;
import org.sopt.makers.domain.auth.PhoneVerificationType;
import org.sopt.makers.domain.auth.exception.AuthException;
import org.sopt.makers.domain.auth.port.PhoneVerificationRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

  private final PhoneVerificationRepositoryPort phoneVerificationRepositoryPort;

  public PhoneVerification createVerification(
      String name, String phone, PhoneVerificationType type) {
    PhoneVerification verification = PhoneVerification.create(name, phone, type);
    return phoneVerificationRepositoryPort.save(verification);
  }

  public void verifyCode(String phone, String code, PhoneVerificationType type) {
    PhoneVerification verification = findLatestOrThrow(phone, type);

    if (verification.isExpired()) {
      phoneVerificationRepositoryPort.delete(verification);
      throw new AuthException(EXPIRED_PHONE_VERIFICATION);
    }

    if (!verification.getVerificationCode().getCode().equals(code)) {
      throw new AuthException(INVALID_PHONE_VERIFICATION_CODE);
    }

    phoneVerificationRepositoryPort.save(verification.verify());
  }

  @Transactional(readOnly = true)
  public void validateVerified(String phone, PhoneVerificationType type) {
    PhoneVerification verification = findLatestOrThrow(phone, type);
    if (!verification.isVerified()) {
      throw new AuthException(PHONE_NOT_VERIFIED);
    }
  }

  public void deleteVerification(PhoneVerification verification) {
    phoneVerificationRepositoryPort.delete(verification);
  }

  @Transactional(readOnly = true)
  public PhoneVerification findVerifiedOrThrow(String phone, PhoneVerificationType type) {
    PhoneVerification verification = findLatestOrThrow(phone, type);
    if (!verification.isVerified()) {
      throw new AuthException(PHONE_NOT_VERIFIED);
    }
    return verification;
  }

  private PhoneVerification findLatestOrThrow(String phone, PhoneVerificationType type) {
    return phoneVerificationRepositoryPort
        .findLatestByPhoneAndType(phone, type)
        .orElseThrow(() -> new AuthException(NOT_FOUND_PHONE_VERIFICATION));
  }
}
