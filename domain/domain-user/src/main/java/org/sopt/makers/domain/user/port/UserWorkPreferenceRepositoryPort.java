package org.sopt.makers.domain.user.port;

import org.sopt.makers.domain.user.WorkPreference;

public interface UserWorkPreferenceRepositoryPort {

  /** 유저의 작업 성향을 저장하거나 갱신한다. */
  void upsert(Long userId, WorkPreference workPreference);
}
