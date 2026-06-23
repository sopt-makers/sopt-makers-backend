package org.sopt.makers.storage.db.user.adapter;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.user.WorkPreference;
import org.sopt.makers.domain.user.port.UserWorkPreferenceRepositoryPort;
import org.sopt.makers.storage.db.user.entity.UserEntity;
import org.sopt.makers.storage.db.user.entity.UserWorkPreferenceEntity;
import org.sopt.makers.storage.db.user.repository.UserJpaRepository;
import org.sopt.makers.storage.db.user.repository.UserWorkPreferenceJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserWorkPreferenceRepositoryAdapter implements UserWorkPreferenceRepositoryPort {

  private final UserWorkPreferenceJpaRepository userWorkPreferenceJpaRepository;
  private final UserJpaRepository userJpaRepository;

  @Transactional
  @Override
  public void upsert(final Long userId, final WorkPreference workPreference) {
    userWorkPreferenceJpaRepository
        .findByUser_Id(userId)
        .ifPresentOrElse(
            entity -> entity.update(workPreference),
            () -> {
              UserEntity userRef = userJpaRepository.getReferenceById(userId);
              userWorkPreferenceJpaRepository.save(
                  UserWorkPreferenceEntity.from(userRef, workPreference));
            });
  }
}
