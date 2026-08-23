package org.sopt.makers.storage.db.playground.resolution.repository;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.storage.db.playground.resolution.entity.UserResolutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserResolutionJpaRepository extends JpaRepository<UserResolutionEntity, Long> {

  Optional<UserResolutionEntity> findByUserIdAndGeneration(Long userId, int generation);

  boolean existsByUserIdAndGeneration(Long userId, int generation);

  List<UserResolutionEntity> findAllByGeneration(int generation);
}
