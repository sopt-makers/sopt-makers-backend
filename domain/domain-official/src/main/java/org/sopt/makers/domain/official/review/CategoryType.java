package org.sopt.makers.domain.official.review;

import java.util.Arrays;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.official.review.exception.ReviewException;
import org.sopt.makers.domain.official.review.exception.ReviewFailure;

@Getter
@RequiredArgsConstructor
public enum CategoryType {
  ACTIVITY("전체 활동", true, false),
  RECRUITING("서류/면접", false, true),
  SEMINAR("세미나", false, false),
  PROJECT("프로젝트", false, false),
  STUDY("스터디", false, false),
  OTHER("기타", false, false);

  private final String displayName;
  private final boolean requiresSubActivities;
  private final boolean recruiting;

  public static CategoryType from(String displayName) {
    return fromSafely(displayName)
        .orElseThrow(() -> new ReviewException(ReviewFailure.INVALID_CATEGORY));
  }

  public static Optional<CategoryType> fromSafely(String displayName) {
    if (displayName == null || displayName.isBlank()) {
      return Optional.empty();
    }
    return Arrays.stream(values()).filter(type -> type.displayName.equals(displayName)).findAny();
  }
}
