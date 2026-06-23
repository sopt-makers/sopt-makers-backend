package org.sopt.makers.storage.db.user.repository;

import org.sopt.makers.storage.db.user.entity.UserCareerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCareerJpaRepository extends JpaRepository<UserCareerEntity, Long> {

  void deleteAllByUserId(Long userId);
}
