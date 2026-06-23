package org.sopt.makers.domain.user.port;

import java.util.List;
import org.sopt.makers.domain.user.UserLink;

public interface UserLinkRepositoryPort {

  /** 유저의 링크를 전체 교체한다. 기존 링크를 모두 삭제하고 새 목록으로 재등록한다. */
  List<UserLink> replaceAll(Long userId, List<UserLink> links);
}
