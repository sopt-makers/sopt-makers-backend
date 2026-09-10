package org.sopt.makers.domain.user.port;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.sopt.makers.domain.user.UserLink;

public interface UserLinkRepositoryPort {

  /** 유저의 링크를 전체 교체한다. 기존 링크를 모두 삭제하고 새 목록으로 재등록한다. */
  List<UserLink> replaceAll(Long userId, List<UserLink> links);

  Optional<UserLink> findById(Long linkId);

  void deleteById(Long linkId);

  /** 유저별 전체 링크 목록을 벌크 조회한다. 프로필 조회 응답용. */
  Map<Long, List<UserLink>> findAllLinksByUserIds(List<Long> userIds);

  /** 단건 프로필 상세 조회용 — 해당 유저의 전체 링크 목록. */
  List<UserLink> findAllByUserId(Long userId);
}
