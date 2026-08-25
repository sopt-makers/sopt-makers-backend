package org.sopt.makers.storage.db.app.fortune.repository;

import java.util.Optional;
import org.sopt.makers.storage.db.app.fortune.entity.UserFortuneEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFortuneJpaRepository extends JpaRepository<UserFortuneEntity, Long> {

  Optional<UserFortuneEntity> findByUserId(Long userId);
}
