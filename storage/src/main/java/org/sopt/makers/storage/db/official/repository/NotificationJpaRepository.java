package org.sopt.makers.storage.db.official.repository;

import java.util.List;
import org.sopt.makers.storage.db.official.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationJpaRepository extends JpaRepository<NotificationEntity, Long> {

  boolean existsByEmailAndGeneration(String email, Integer generation);

  List<NotificationEntity> findByGeneration(Integer generation);
}
