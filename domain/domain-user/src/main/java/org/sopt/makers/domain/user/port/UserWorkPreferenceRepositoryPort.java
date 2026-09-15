package org.sopt.makers.domain.user.port;

import java.util.List;
import java.util.Map;
import org.sopt.makers.domain.user.WorkPreference;

public interface UserWorkPreferenceRepositoryPort {

  /** 유저의 작업 성향을 저장하거나 갱신한다. */
  void upsert(Long userId, WorkPreference workPreference);

  /** 여러 유저의 작업 성향을 한 번에 조회한다. 설정되지 않은 유저는 결과 맵에 포함되지 않는다. */
  Map<Long, WorkPreference> findAllByUserIds(List<Long> userIds);
}
