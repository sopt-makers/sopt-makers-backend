package org.sopt.makers.storage.db.auth.adapter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.auth.PhoneVerification;
import org.sopt.makers.domain.auth.PhoneVerificationType;
import org.sopt.makers.domain.auth.port.PhoneVerificationRepositoryPort;
import org.sopt.makers.storage.db.auth.entity.PhoneVerificationEntity;
import org.sopt.makers.storage.db.auth.repository.PhoneVerificationJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhoneVerificationRepositoryAdapter implements PhoneVerificationRepositoryPort {

  private final PhoneVerificationJpaRepository phoneVerificationJpaRepository;

  @Override
  public Optional<PhoneVerification> findLatestByPhoneAndType(
      String phone, PhoneVerificationType type) {
    return phoneVerificationJpaRepository
        .findTopByPhoneAndTypeOrderByCreatedAtDesc(phone, type)
        .map(PhoneVerificationEntity::toDomain);
  }

  @Override
  public Optional<PhoneVerification> findLatestByPhoneAndTypeAndName(
      String phone, PhoneVerificationType type, String name) {
    return phoneVerificationJpaRepository
        .findTopByPhoneAndTypeAndNameOrderByCreatedAtDesc(phone, type, name)
        .map(PhoneVerificationEntity::toDomain);
  }

  @Transactional
  @Override
  public PhoneVerification save(PhoneVerification phoneVerification) {
    return phoneVerificationJpaRepository
        .save(PhoneVerificationEntity.fromDomain(phoneVerification))
        .toDomain();
  }

  @Transactional
  @Override
  public void delete(PhoneVerification phoneVerification) {
    phoneVerificationJpaRepository.deleteByNameAndPhoneAndCodeAndType(
        phoneVerification.getName(),
        phoneVerification.getPhone(),
        phoneVerification.getVerificationCode().getCode(),
        phoneVerification.getVerificationType());
  }
}
