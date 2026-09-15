package org.sopt.makers.storage.db.playground.member.profile.adapter;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.member.profile.port.UserActivityCheckPort;
import org.sopt.makers.storage.db.playground.member.profile.entity.UserActivityCheckEntity;
import org.sopt.makers.storage.db.playground.member.profile.repository.UserActivityCheckJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserActivityCheckRepositoryAdapter implements UserActivityCheckPort {

  private final UserActivityCheckJpaRepository userActivityCheckJpaRepository;

  @Override
  public boolean isEditActivitiesAble(Long userId) {
    return userActivityCheckJpaRepository
        .findByUserId(userId)
        .map(UserActivityCheckEntity::getEditActivitiesAble)
        .orElse(true);
  }

  @Transactional
  @Override
  public void updateEditActivitiesAble(Long userId, boolean isCheck) {
    userActivityCheckJpaRepository
        .findByUserId(userId)
        .ifPresentOrElse(
            entity -> entity.updateEditActivitiesAble(isCheck),
            () ->
                userActivityCheckJpaRepository.save(
                    UserActivityCheckEntity.create(userId, isCheck)));
  }
}
