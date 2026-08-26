package org.sopt.makers.storage.db.app.push.repository;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.storage.db.app.push.entity.PushTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PushTokenJpaRepository extends JpaRepository<PushTokenEntity, Long> {

  boolean existsByUserIdAndToken(Long userId, String token);

  Optional<PushTokenEntity> findByUserIdAndToken(Long userId, String token);

  List<PushTokenEntity> findAllByUserId(Long userId);
}
