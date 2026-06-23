package org.sopt.makers.domain.user.port;

import java.util.List;
import org.sopt.makers.domain.user.Activity;

public interface UserActivityHistoryRepositoryPort {

  List<Activity> findByUserId(Long userId);

  Activity save(Long userId, Activity activity);

  List<Activity> saveAll(Long userId, List<Activity> activities);

  void deleteByUserId(Long userId);
}
