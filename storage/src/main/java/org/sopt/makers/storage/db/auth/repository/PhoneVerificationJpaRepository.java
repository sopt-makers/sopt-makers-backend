package org.sopt.makers.storage.db.auth.repository;

import java.util.Optional;
import org.sopt.makers.domain.auth.PhoneVerificationType;
import org.sopt.makers.storage.db.auth.entity.PhoneVerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneVerificationJpaRepository
    extends JpaRepository<PhoneVerificationEntity, Long> {

  Optional<PhoneVerificationEntity> findTopByPhoneAndTypeOrderByCreatedAtDesc(
      String phone, PhoneVerificationType type);

  Optional<PhoneVerificationEntity> findTopByPhoneAndTypeAndNameOrderByCreatedAtDesc(
      String phone, PhoneVerificationType type, String name);

  void deleteByNameAndPhoneAndCodeAndType(
      String name, String phone, String code, PhoneVerificationType type);
}
