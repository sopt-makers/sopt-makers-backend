package org.sopt.makers.storage.db.user.adapter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.admin.attendance.port.AdminLectureUserPort;
import org.sopt.makers.storage.db.user.entity.UserActivityHistoryEntity;
import org.sopt.makers.storage.db.user.repository.UserActivityHistoryJpaRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminLectureUserAdapter implements AdminLectureUserPort {

  private final UserActivityHistoryJpaRepository activityJpaRepository;

  @Override
  public List<Long> findUserIdsByGenerationAndPart(int generation, Part part) {
    List<UserActivityHistoryEntity> activities =
        (part == null || part == Part.ALL)
            ? activityJpaRepository.findByGenerationAndIsSopt(generation, true)
            : activityJpaRepository.findByGenerationAndPartAndIsSopt(generation, part, true);
    return activities.stream().map(a -> a.getUser().getId()).toList();
  }
}
