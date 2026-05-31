package org.sopt.makers.domain.auth.port;

import java.util.Optional;
import org.sopt.makers.domain.auth.PhoneVerification;
import org.sopt.makers.domain.auth.PhoneVerificationType;

public interface PhoneVerificationRepositoryPort {

  Optional<PhoneVerification> findLatestByPhoneAndType(String phone, PhoneVerificationType type);

  Optional<PhoneVerification> findLatestByPhoneAndTypeAndName(
      String phone, PhoneVerificationType type, String name);

  PhoneVerification save(PhoneVerification phoneVerification);

  void delete(PhoneVerification phoneVerification);
}
