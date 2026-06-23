package org.sopt.makers.storage.db.user.repository;

import java.util.Optional;
import org.sopt.makers.storage.db.user.entity.UserWorkPreferenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserWorkPreferenceJpaRepository
    extends JpaRepository<UserWorkPreferenceEntity, Long> {

  Optional<UserWorkPreferenceEntity> findByUser_Id(Long userId);
}
