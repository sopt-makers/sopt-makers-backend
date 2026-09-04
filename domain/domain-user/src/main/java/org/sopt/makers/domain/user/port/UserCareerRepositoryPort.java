package org.sopt.makers.domain.user.port;

import java.util.List;
import org.sopt.makers.domain.user.UserCareer;

public interface UserCareerRepositoryPort {

  /** 유저의 커리어를 전체 교체한다. 기존 커리어를 모두 삭제하고 새 목록으로 재등록한다. */
  List<UserCareer> replaceAll(Long userId, List<UserCareer> careers);

  /** 유저별 가장 최근(startDate desc, id desc) 커리어 1건씩을 조회한다. */
  List<UserCareer> findLastCareersByUserIds(List<Long> userIds);
}
