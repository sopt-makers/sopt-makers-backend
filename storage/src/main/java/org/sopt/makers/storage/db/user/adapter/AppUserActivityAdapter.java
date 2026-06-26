package org.sopt.makers.storage.db.user.adapter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.admin.app.AppUserActivity;
import org.sopt.makers.domain.admin.app.port.AppUserActivityPort;
import org.sopt.makers.storage.db.user.entity.UserActivityHistoryEntity;
import org.sopt.makers.storage.db.user.repository.UserActivityHistoryJpaRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppUserActivityAdapter implements AppUserActivityPort {

  private final UserActivityHistoryJpaRepository activityJpaRepository;

  @Override
  public Optional<AppUserActivity> findCurrentActivity(Long userId) {
    return activityJpaRepository
        .findFirstByUserIdOrderByGenerationDesc(userId)
        .map(this::toCurrentActivity);
  }

  private AppUserActivity toCurrentActivity(UserActivityHistoryEntity activity) {
    return new AppUserActivity(
        activity.getUser().getId(), activity.getGeneration(), activity.getPart());
  }
}
