package org.sopt.makers.domain.user.port;

import java.util.Optional;
import org.sopt.makers.domain.user.UserRegisterInfo;

public interface UserRegisterInfoRepositoryPort {

  Optional<UserRegisterInfo> findByPhone(String phone);

  UserRegisterInfo save(UserRegisterInfo info);

  void delete(UserRegisterInfo info);
}
