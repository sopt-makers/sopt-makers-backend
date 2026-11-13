package org.sopt.makers.domain.crew.dining.result;

import java.util.List;
import org.sopt.makers.domain.crew.dining.model.DiningUser;
import org.sopt.makers.domain.crew.dining.query.PageMetaDto;

public record GetAttendeeManagementResult(List<Attendee> attendees, PageMetaDto meta) {

  public record Attendee(
      Long id,
      long attendanceNumber,
      String name,
      String profileImage,
      Integer recentGeneration,
      String phone) {
    public static Attendee of(DiningUser user, long attendanceNumber) {
      return new Attendee(
          user.getId(),
          attendanceNumber,
          user.getName(),
          user.getProfileImage(),
          user.getRecentGeneration(),
          user.getPhone());
    }
  }
}
