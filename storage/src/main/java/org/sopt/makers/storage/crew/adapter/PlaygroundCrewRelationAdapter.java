package org.sopt.makers.storage.crew.adapter;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.crew.meeting.Meeting;
import org.sopt.makers.domain.crew.meeting.MeetingStatus;
import org.sopt.makers.domain.crew.meeting.MemberRole;
import org.sopt.makers.domain.crew.meeting.service.MeetingService;
import org.sopt.makers.domain.playground.member.profile.port.PlaygroundCrewRelationPort;
import org.springframework.stereotype.Component;

/** domain-crew의 MeetingService를 어댑터로 직결한다(CLAUDE.md 5.4, FeignClient 대체). */
@Component
@RequiredArgsConstructor
public class PlaygroundCrewRelationAdapter implements PlaygroundCrewRelationPort {

  private static final int RELATED_USER_MEETING_PAGE_NO = 1;
  private static final int RELATED_USER_MEETING_LIMIT = 100;

  private final MeetingService meetingService;
  private final Clock clock;

  @Override
  public List<Long> findRelatedUserIds(Long userId) {
    PageResult<MeetingService.JoinedMeeting> joinedMeetings =
        meetingService.findJoinedMeetings(
            userId, RELATED_USER_MEETING_PAGE_NO, RELATED_USER_MEETING_LIMIT);

    Set<Long> relatedUserIds = new LinkedHashSet<>();
    for (MeetingService.JoinedMeeting joined : joinedMeetings.content()) {
      Long meetingId = joined.summary().meeting().id();
      meetingService.getParticipants(meetingId, userId).stream()
          .map(detail -> detail.member().userId())
          .filter(participantId -> !participantId.equals(userId))
          .forEach(relatedUserIds::add);
    }
    return List.copyOf(relatedUserIds);
  }

  @Override
  public CrewMeetingPage findJoinedMeetings(Long userId, int pageNo, int limit) {
    PageResult<MeetingService.JoinedMeeting> result =
        meetingService.findJoinedMeetings(userId, pageNo, limit);

    List<CrewMeetingItem> items = result.content().stream().map(this::toItem).toList();

    return new CrewMeetingPage(
        items,
        result.page(),
        result.limit(),
        result.totalElements(),
        result.totalPages(),
        result.hasNext(),
        result.hasPrevious());
  }

  private CrewMeetingItem toItem(MeetingService.JoinedMeeting joined) {
    Meeting meeting = joined.summary().meeting();
    boolean isMeetingLeader = joined.member().role() == MemberRole.LEADER;
    String imageUrl = meeting.images().isEmpty() ? null : meeting.images().get(0).url();
    boolean isActiveMeeting =
        meeting.getMeetingStatus(LocalDateTime.now(clock)) != MeetingStatus.RECRUITMENT_COMPLETE;

    return new CrewMeetingItem(
        meeting.id(),
        isMeetingLeader,
        meeting.title(),
        imageUrl,
        meeting.category().getValue(),
        isActiveMeeting,
        meeting.startDate(),
        meeting.endDate());
  }
}
