package org.sopt.makers.storage.db.playground.resolution.repository;

import java.util.Optional;
import org.sopt.makers.storage.db.playground.resolution.entity.UserResolutionLuckyPickEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserResolutionLuckyPickJpaRepository extends JpaRepository<UserResolutionLuckyPickEntity, Long> {

    Optional<UserResolutionLuckyPickEntity> findByUserId(Long userId);

    boolean existsByUserIdAndHasDrawnTrue(Long userId);
}
