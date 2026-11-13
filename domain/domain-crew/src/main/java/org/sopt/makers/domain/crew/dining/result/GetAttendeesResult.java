package org.sopt.makers.domain.crew.dining.result;

import java.util.List;
import org.sopt.makers.domain.crew.dining.model.DiningUser;

public record GetAttendeesResult(List<Attendee> attendees) {

  public record Attendee(Long id, String name, String profileImage) {
    public static Attendee of(DiningUser user) {
      return new Attendee(user.getId(), user.getName(), user.getProfileImage());
    }
  }
}
