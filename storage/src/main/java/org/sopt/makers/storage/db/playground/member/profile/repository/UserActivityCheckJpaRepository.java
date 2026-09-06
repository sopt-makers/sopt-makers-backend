package org.sopt.makers.storage.db.playground.member.profile.repository;

import java.util.Optional;
import org.sopt.makers.storage.db.playground.member.profile.entity.UserActivityCheckEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserActivityCheckJpaRepository extends JpaRepository<UserActivityCheckEntity, Long> {

  Optional<UserActivityCheckEntity> findByUserId(Long userId);
}
