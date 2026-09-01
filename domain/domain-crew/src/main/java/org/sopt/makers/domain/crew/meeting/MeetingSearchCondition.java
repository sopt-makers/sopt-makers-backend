package org.sopt.makers.domain.crew.meeting;

import java.time.LocalDateTime;

public record MeetingSearchCondition(
    String search, MeetingCategory category, MeetingStatus status, LocalDateTime now) {

  public MeetingSearchCondition {
    search = search == null || search.isBlank() ? null : search.trim();
  }
}
