package org.sopt.makers.storage.db.user.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.user.UserCareer;
import org.sopt.makers.domain.user.port.UserCareerRepositoryPort;
import org.sopt.makers.storage.db.user.entity.UserCareerEntity;
import org.sopt.makers.storage.db.user.repository.UserCareerJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserCareerRepositoryAdapter implements UserCareerRepositoryPort {

  private final UserCareerJpaRepository userCareerJpaRepository;

  @Override
  public List<UserCareer> findByUserId(Long userId) {
    return userCareerJpaRepository.findAllByUserId(userId).stream()
        .map(UserCareerEntity::toDomain)
        .toList();
  }

  @Override
  public List<UserCareer> findByUserIdIn(List<Long> userIds) {
    if (userIds == null || userIds.isEmpty()) {
      return List.of();
    }
    return userCareerJpaRepository.findAllByUserIdIn(userIds).stream()
        .map(UserCareerEntity::toDomain)
        .toList();
  }

  @Transactional
  @Override
  public List<UserCareer> replaceAll(final Long userId, final List<UserCareer> careers) {
    userCareerJpaRepository.deleteAllByUserId(userId);
    List<UserCareerEntity> entities =
        careers.stream()
            .map(
                career ->
                    UserCareerEntity.from(
                        UserCareer.of(
                            null,
                            userId,
                            career.companyName(),
                            career.title(),
                            career.startDate(),
                            career.endDate(),
                            career.isCurrent())))
            .toList();
    return userCareerJpaRepository.saveAll(entities).stream()
        .map(UserCareerEntity::toDomain)
        .toList();
  }
}
