package org.sopt.makers.storage.db.user.repository;

import java.util.Optional;
import org.sopt.makers.storage.db.user.entity.UserRegisterInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRegisterInfoJpaRepository extends JpaRepository<UserRegisterInfoEntity, Long> {

  Optional<UserRegisterInfoEntity> findByPhone(String phone);
}
