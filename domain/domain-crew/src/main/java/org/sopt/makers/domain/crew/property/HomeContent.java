package org.sopt.makers.domain.crew.property;

import java.util.List;

public record HomeContent(String title, List<Long> meetingIds) {

  public HomeContent {
    meetingIds = meetingIds == null ? List.of() : List.copyOf(meetingIds);
  }
}
