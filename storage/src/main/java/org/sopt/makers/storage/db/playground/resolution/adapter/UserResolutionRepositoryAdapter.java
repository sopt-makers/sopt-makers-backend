package org.sopt.makers.storage.db.playground.resolution.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.resolution.UserResolution;
import org.sopt.makers.domain.playground.resolution.port.UserResolutionRepositoryPort;
import org.sopt.makers.storage.db.playground.resolution.entity.UserResolutionEntity;
import org.sopt.makers.storage.db.playground.resolution.repository.UserResolutionJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserResolutionRepositoryAdapter implements UserResolutionRepositoryPort {

  private final UserResolutionJpaRepository userResolutionJpaRepository;

  @Override
  public Optional<UserResolution> findByUserIdAndGeneration(Long userId, int generation) {
    return userResolutionJpaRepository
        .findByUserIdAndGeneration(userId, generation)
        .map(UserResolutionEntity::toDomain);
  }

  @Override
  public boolean existsByUserIdAndGeneration(Long userId, int generation) {
    return userResolutionJpaRepository.existsByUserIdAndGeneration(userId, generation);
  }

  @Override
  public List<UserResolution> findAllByGeneration(int generation) {
    return userResolutionJpaRepository.findAllByGeneration(generation).stream()
        .map(UserResolutionEntity::toDomain)
        .toList();
  }

  @Override
  @Transactional
  public UserResolution save(UserResolution resolution) {
    return userResolutionJpaRepository.save(UserResolutionEntity.from(resolution)).toDomain();
  }

  @Override
  @Transactional
  public void delete(UserResolution resolution) {
    userResolutionJpaRepository
        .findByUserIdAndGeneration(resolution.userId(), resolution.generation())
        .ifPresent(userResolutionJpaRepository::delete);
  }
}
