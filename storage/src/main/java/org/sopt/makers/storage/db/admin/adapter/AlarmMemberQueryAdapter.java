package org.sopt.makers.storage.db.admin.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.alarm.port.AlarmMemberQueryPort;
import org.sopt.makers.storage.db.user.entity.UserActivityHistoryEntity;
import org.sopt.makers.storage.db.user.entity.UserEntity;
import org.sopt.makers.storage.db.user.repository.UserActivityHistoryJpaRepository;
import org.sopt.makers.storage.db.user.repository.UserJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlarmMemberQueryAdapter implements AlarmMemberQueryPort {

  private final UserJpaRepository userJpaRepository;
  private final UserActivityHistoryJpaRepository userActivityHistoryJpaRepository;

  @Override
  public List<Long> findAllUserIds() {
    return userJpaRepository.findAll().stream()
        .map(UserEntity::getId)
        .toList();
  }

  @Override
  public List<Long> findActiveUserIdsByGenerationAndPart(int generation, Part part) {
    List<UserActivityHistoryEntity> activities =
        part.equals(Part.ALL)
            ? userActivityHistoryJpaRepository.findByGenerationAndIsSopt(generation, true)
            : userActivityHistoryJpaRepository.findByGenerationAndPartAndIsSopt(generation, part, true);
    return activities.stream()
        .map(a -> a.getUser().getId())
        .toList();
  }
}
