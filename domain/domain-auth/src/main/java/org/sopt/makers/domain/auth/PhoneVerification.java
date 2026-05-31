package org.sopt.makers.domain.auth;

import static lombok.AccessLevel.PRIVATE;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder(access = PRIVATE)
public record PhoneVerification(
    Long id,
    String name,
    String phone,
    PhoneVerificationType verificationType,
    VerificationCode verificationCode,
    LocalDateTime createdAt,
    boolean isVerified) {

  private static final int VERIFICATION_EXPIRY_MINUTES = 3;

  public static PhoneVerification create(String name, String phone, PhoneVerificationType type) {
    return PhoneVerification.builder()
        .name(name)
        .phone(phone)
        .verificationType(type)
        .verificationCode(VerificationCode.random())
        .createdAt(LocalDateTime.now())
        .isVerified(false)
        .build();
  }

  public static PhoneVerification of(
      Long id,
      String name,
      String phone,
      PhoneVerificationType type,
      String code,
      LocalDateTime createdAt,
      boolean isVerified) {
    return PhoneVerification.builder()
        .id(id)
        .name(name)
        .phone(phone)
        .verificationType(type)
        .verificationCode(VerificationCode.of(code))
        .createdAt(createdAt)
        .isVerified(isVerified)
        .build();
  }

  public boolean isExpired() {
    return LocalDateTime.now().isAfter(createdAt.plusMinutes(VERIFICATION_EXPIRY_MINUTES));
  }

  public PhoneVerification verify() {
    return PhoneVerification.builder()
        .id(this.id)
        .name(this.name)
        .phone(this.phone)
        .verificationType(this.verificationType)
        .verificationCode(this.verificationCode)
        .createdAt(this.createdAt)
        .isVerified(true)
        .build();
  }

  public record VerificationCode(String code) {

    private static final int CODE_SIZE = 6;

    static VerificationCode random() {
      return new VerificationCode(generateRandomCode());
    }

    static VerificationCode of(String code) {
      return new VerificationCode(code);
    }

    private static String generateRandomCode() {
      SecureRandom random = new SecureRandom();
      StringBuilder builder = new StringBuilder(CODE_SIZE);
      for (int i = 0; i < CODE_SIZE; i++) {
        builder.append(random.nextInt(10));
      }
      return builder.toString();
    }
  }
}
