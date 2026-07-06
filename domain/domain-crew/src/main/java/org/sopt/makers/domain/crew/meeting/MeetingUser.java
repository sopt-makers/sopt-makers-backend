package org.sopt.makers.domain.crew.meeting;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.user.Activity;

public record MeetingUser(Long id, String name, String profileImage, List<Activity> activities) {

  public MeetingUser {
    activities = activities == null ? List.of() : List.copyOf(activities);
  }

  public Optional<Activity> findLatestActivity() {
    return activities.stream().max(Comparator.comparingInt(Activity::generation));
  }

  public Optional<Activity> findActivityByGeneration(int generation) {
    return activities.stream().filter(activity -> activity.generation() == generation).findFirst();
  }
}
