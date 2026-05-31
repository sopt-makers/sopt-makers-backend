package org.sopt.makers.storage.db.auth.entity;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.makers.domain.auth.PhoneVerification;
import org.sopt.makers.domain.auth.PhoneVerificationType;
import org.sopt.makers.storage.db.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "phone_verifications")
public class PhoneVerificationEntity extends BaseEntity {

  private String name;

  @Column(nullable = false)
  private String phone;

  @Column(nullable = false)
  private String code;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PhoneVerificationType type;

  @Column(nullable = false)
  private boolean isVerified;

  @Builder(access = PRIVATE)
  private PhoneVerificationEntity(
      String name, String phone, String code, PhoneVerificationType type, boolean isVerified) {
    this.name = name;
    this.phone = phone;
    this.code = code;
    this.type = type;
    this.isVerified = isVerified;
  }

  public static PhoneVerificationEntity create(
      String name, String phone, String code, PhoneVerificationType type) {
    return PhoneVerificationEntity.builder()
        .name(name)
        .phone(phone)
        .code(code)
        .type(type)
        .isVerified(false)
        .build();
  }

  public void verify() {
    this.isVerified = true;
  }

  public PhoneVerification toDomain() {
    return PhoneVerification.of(getId(), name, phone, type, code, getCreatedAt(), isVerified);
  }

  public static PhoneVerificationEntity fromDomain(PhoneVerification verification) {
    PhoneVerificationEntity entity =
        PhoneVerificationEntity.create(
            verification.getName(),
            verification.getPhone(),
            verification.getVerificationCode().getCode(),
            verification.getVerificationType());
    if (verification.isVerified()) {
      entity.verify();
    }
    if (verification.getId() != null) {
      entity.setId(verification.getId());
    }
    return entity;
  }
}
