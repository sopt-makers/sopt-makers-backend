package org.sopt.makers.storage.db.user.repository;

import org.sopt.makers.storage.db.user.entity.UserLinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLinkJpaRepository extends JpaRepository<UserLinkEntity, Long> {

  void deleteAllByUserId(Long userId);
}
