package org.sopt.makers.domain.user.port;

import java.util.List;
import org.sopt.makers.domain.user.User;

public interface PlaygroundAskUserPort {

  String getName(Long userId);

  String getPhoneNumber(Long userId);

  int getLastSoptGeneration(Long userId);

  /** 에스크 질문 목록 조회 시 작성자/수신자 정보를 N+1 없이 묶어 조회하기 위한 벌크 조회. */
  List<User> findAllWithActivitiesByIds(List<Long> userIds);
}
