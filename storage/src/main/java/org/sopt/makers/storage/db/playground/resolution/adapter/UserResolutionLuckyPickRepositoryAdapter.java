package org.sopt.makers.storage.db.playground.resolution.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.resolution.UserResolutionLuckyPick;
import org.sopt.makers.domain.playground.resolution.exception.ResolutionException;
import org.sopt.makers.domain.playground.resolution.exception.ResolutionFailure;
import org.sopt.makers.domain.playground.resolution.port.UserResolutionLuckyPickRepositoryPort;
import org.sopt.makers.storage.db.playground.resolution.entity.UserResolutionLuckyPickEntity;
import org.sopt.makers.storage.db.playground.resolution.repository.UserResolutionLuckyPickJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserResolutionLuckyPickRepositoryAdapter
    implements UserResolutionLuckyPickRepositoryPort {

  private final UserResolutionLuckyPickJpaRepository luckyPickJpaRepository;

  @Override
  public Optional<UserResolutionLuckyPick> findByUserId(Long userId) {
    return luckyPickJpaRepository.findByUserId(userId).map(UserResolutionLuckyPickEntity::toDomain);
  }

  @Override
  public boolean existsByUserIdAndHasDrawnTrue(Long userId) {
    return luckyPickJpaRepository.existsByUserIdAndHasDrawnTrue(userId);
  }

  @Override
  public long count() {
    return luckyPickJpaRepository.count();
  }

  @Override
  @Transactional
  public UserResolutionLuckyPick save(UserResolutionLuckyPick luckyPick) {
    UserResolutionLuckyPickEntity entity =
        luckyPick.id() != null
            ? luckyPickJpaRepository
                .findById(luckyPick.id())
                .map(
                    e -> {
                      e.update(luckyPick);
                      return e;
                    })
                .orElseThrow(() -> new ResolutionException(ResolutionFailure.NOT_FOUND_RESOLUTION))
            : UserResolutionLuckyPickEntity.from(luckyPick);
    return luckyPickJpaRepository.save(entity).toDomain();
  }

  @Override
  @Transactional
  public List<UserResolutionLuckyPick> saveAll(List<UserResolutionLuckyPick> luckyPicks) {
    List<UserResolutionLuckyPickEntity> entities =
        luckyPicks.stream()
            .map(
                luckyPick ->
                    luckyPick.id() != null
                        ? luckyPickJpaRepository
                            .findById(luckyPick.id())
                            .map(
                                e -> {
                                  e.update(luckyPick);
                                  return e;
                                })
                            .orElseThrow(
                                () ->
                                    new ResolutionException(ResolutionFailure.NOT_FOUND_RESOLUTION))
                        : UserResolutionLuckyPickEntity.from(luckyPick))
            .toList();
    return luckyPickJpaRepository.saveAll(entities).stream()
        .map(UserResolutionLuckyPickEntity::toDomain)
        .toList();
  }
}
