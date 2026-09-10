package org.sopt.makers.domain.playground.member.profile.port;

import java.time.LocalDateTime;
import java.util.List;

public interface PlaygroundCrewRelationPort {

  /** 유저가 참여 중인 크루 모임의 다른 참여자 id 목록(자기 자신 제외, 중복 제거). */
  List<Long> findRelatedUserIds(Long userId);

  /** 유저가 참여 중인 크루 모임 목록을 페이지네이션하여 조회한다. */
  CrewMeetingPage findJoinedMeetings(Long userId, int pageNo, int limit);

  record CrewMeetingPage(
      List<CrewMeetingItem> meetings,
      int page,
      int limit,
      long totalElements,
      int totalPages,
      boolean hasNext,
      boolean hasPrevious) {}

  record CrewMeetingItem(
      Long id,
      boolean isMeetingLeader,
      String title,
      String imageUrl,
      String category,
      boolean isActiveMeeting,
      LocalDateTime startDate,
      LocalDateTime endDate) {}
}
