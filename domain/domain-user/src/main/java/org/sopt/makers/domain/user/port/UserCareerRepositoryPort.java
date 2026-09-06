package org.sopt.makers.domain.user.port;

import java.util.List;
import org.sopt.makers.domain.user.UserCareer;

public interface UserCareerRepositoryPort {

  /** 유저의 커리어를 전체 교체한다. 기존 커리어를 모두 삭제하고 새 목록으로 재등록한다. */
  List<UserCareer> replaceAll(Long userId, List<UserCareer> careers);

  List<UserCareer> findByUserId(Long userId);

  List<UserCareer> findByUserIdIn(List<Long> userIds);
}
