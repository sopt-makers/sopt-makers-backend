package org.sopt.makers.storage.db.user.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.port.UserActivityHistoryRepositoryPort;
import org.sopt.makers.storage.db.user.entity.UserActivityHistoryEntity;
import org.sopt.makers.storage.db.user.entity.UserEntity;
import org.sopt.makers.storage.db.user.repository.UserActivityHistoryJpaRepository;
import org.sopt.makers.storage.db.user.repository.UserJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserActivityHistoryRepositoryAdapter implements UserActivityHistoryRepositoryPort {

  private final UserActivityHistoryJpaRepository activityJpaRepository;
  private final UserJpaRepository userJpaRepository;

  @Override
  public List<Activity> findByUserId(Long userId) {
    return activityJpaRepository.findByUserId(userId).stream()
        .map(UserActivityHistoryEntity::toDomain)
        .toList();
  }

  @Transactional
  @Override
  public Activity save(Long userId, Activity activity) {
    UserEntity userRef = userJpaRepository.getReferenceById(userId);
    UserActivityHistoryEntity entity =
        UserActivityHistoryEntity.create(
            userRef,
            activity.getGeneration(),
            activity.getTeam(),
            activity.getPart(),
            activity.getRole(),
            activity.isSopt(),
            activity.getAttendanceScore());
    return activityJpaRepository.save(entity).toDomain();
  }

  @Transactional
  @Override
  public List<Activity> saveAll(Long userId, List<Activity> activities) {
    UserEntity userRef = userJpaRepository.getReferenceById(userId);
    List<UserActivityHistoryEntity> entities =
        activities.stream()
            .map(
                a ->
                    UserActivityHistoryEntity.create(
                        userRef,
                        a.getGeneration(),
                        a.getTeam(),
                        a.getPart(),
                        a.getRole(),
                        a.isSopt(),
                        a.getAttendanceScore()))
            .toList();
    return activityJpaRepository.saveAll(entities).stream()
        .map(UserActivityHistoryEntity::toDomain)
        .toList();
  }

  @Transactional
  @Override
  public void deleteByUserId(Long userId) {
    activityJpaRepository.deleteByUserId(userId);
  }

  @Transactional
  @Override
  public void updateAttendanceScore(Long userId, int generation, Float score) {
    activityJpaRepository.updateAttendanceScore(userId, generation, score);
  }
}
