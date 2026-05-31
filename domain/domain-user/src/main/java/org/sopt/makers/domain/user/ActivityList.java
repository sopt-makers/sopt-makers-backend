package org.sopt.makers.domain.user;

import static org.sopt.makers.domain.user.exception.UserFailure.DUPLICATE_ACTIVITY;
import static org.sopt.makers.domain.user.exception.UserFailure.INVALID_ACTIVITY;
import static org.sopt.makers.domain.user.exception.UserFailure.NOT_FOUND_USER_ACTIVITY;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.sopt.makers.domain.user.exception.UserException;

public record ActivityList(List<Activity> activities) {

  public ActivityList() {
    this(new ArrayList<>());
  }

  public static ActivityList of(List<Activity> activities) {
    List<Activity> sorted = new ArrayList<>(activities);
    sorted.sort(Comparator.comparingInt(Activity::generation));
    return new ActivityList(sorted);
  }

  public ActivityList addActivity(final Activity activity) {
    if (activity == null) {
      throw new UserException(INVALID_ACTIVITY);
    }
    validateNoDuplicate(activity);
    List<Activity> updated = new ArrayList<>(this.activities);
    updated.add(activity);
    return new ActivityList(updated);
  }

  public Activity getFirstActivity() {
    return activities.getFirst();
  }

  public Activity getLastActivity() {
    return activities.getLast();
  }

  public Activity getLastSoptActivity() {
    return activities.stream()
        .filter(Activity::isSopt)
        .reduce((first, second) -> second)
        .orElseThrow(() -> new UserException(NOT_FOUND_USER_ACTIVITY));
  }

  public int getTotalActivitySize() {
    return activities.size();
  }

  private void validateNoDuplicate(final Activity activity) {
    boolean isDuplicate =
        activities.stream()
            .anyMatch(
                a -> a.generation() == activity.generation() && a.isSopt() == activity.isSopt());
    if (isDuplicate) {
      throw new UserException(DUPLICATE_ACTIVITY);
    }
  }
}
