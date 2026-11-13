package org.sopt.makers.domain.crew.dining.result;

import java.time.LocalDate;
import java.util.List;
import org.sopt.makers.domain.crew.dining.query.PageMetaDto;
import org.sopt.makers.domain.crew.meeting.MeetingJoinablePart;

public record GetDiningsResult(List<DiningCard> dinings, PageMetaDto meta) {

  public record DiningCard(
      Long meetingId,
      String title,
      LocalDate diningDate,
      String imageUrl,
      boolean isEnded,
      MeetingJoinablePart[] joinableParts,
      long attendeeCount,
      DiningLeader leader,
      boolean canManageAttendees) {}
}
