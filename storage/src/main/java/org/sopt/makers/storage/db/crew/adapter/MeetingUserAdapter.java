package org.sopt.makers.storage.db.crew.adapter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.crew.meeting.MeetingUser;
import org.sopt.makers.domain.crew.meeting.port.MeetingUserPort;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.storage.db.user.entity.UserActivityHistoryEntity;
import org.sopt.makers.storage.db.user.entity.UserEntity;
import org.sopt.makers.storage.db.user.repository.UserActivityHistoryJpaRepository;
import org.sopt.makers.storage.db.user.repository.UserJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeetingUserAdapter implements MeetingUserPort {

  private final UserJpaRepository userJpaRepository;
  private final UserActivityHistoryJpaRepository userActivityHistoryJpaRepository;

  @Override
  public Optional<MeetingUser> findById(Long userId) {
    return userJpaRepository
        .findById(userId)
        .map(user -> toMeetingUser(user, userActivityHistoryJpaRepository.findByUserId(userId)));
  }

  @Override
  public List<MeetingUser> findAllById(List<Long> userIds) {
    if (userIds == null || userIds.isEmpty()) {
      return List.of();
    }
    List<Long> distinctIds = userIds.stream().distinct().toList();
    Map<Long, List<UserActivityHistoryEntity>> activitiesByUserId =
        userActivityHistoryJpaRepository.findAllByUserIdIn(distinctIds).stream()
            .collect(Collectors.groupingBy(activity -> activity.getUser().getId()));
    return userJpaRepository.findAllById(distinctIds).stream()
        .map(user -> toMeetingUser(user, activitiesByUserId.getOrDefault(user.getId(), List.of())))
        .toList();
  }

  private MeetingUser toMeetingUser(
      UserEntity user, List<UserActivityHistoryEntity> activityEntities) {
    List<Activity> activities =
        activityEntities == null
            ? List.of()
            : activityEntities.stream().map(UserActivityHistoryEntity::toDomain).toList();
    return new MeetingUser(user.getId(), user.getName(), user.getProfileImage(), activities);
  }
}
