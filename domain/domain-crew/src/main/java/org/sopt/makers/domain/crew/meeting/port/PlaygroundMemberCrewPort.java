package org.sopt.makers.domain.crew.meeting.port;

import java.time.LocalDateTime;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.crew.meeting.MeetingJoinInfo;

/** Playground 사용자 프로필이 기존 {@code GET /meeting/v2/org-user} 대신 사용하는 내부 Port. */
public interface PlaygroundMemberCrewPort {

  PageResult<MemberCrewInfo> findUserMeetings(Long userId, int page, int take);

  record MemberCrewInfo(
      Long id,
      boolean meetingLeader,
      String title,
      String subTitle,
      String imageUrl,
      String category,
      LocalDateTime meetingStartDate,
      LocalDateTime meetingEndDate,
      MeetingJoinInfo joinInfo,
      boolean activeMeeting) {}
}
