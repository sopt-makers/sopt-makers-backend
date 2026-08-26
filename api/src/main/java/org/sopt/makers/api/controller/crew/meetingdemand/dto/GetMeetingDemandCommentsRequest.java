package org.sopt.makers.api.controller.crew.meetingdemand.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record GetMeetingDemandCommentsRequest(@Min(1) Integer page, @Min(1) @Max(50) Integer take) {

  private static final int DEFAULT_PAGE = 1;
  private static final int DEFAULT_TAKE = 12;

  public int pageOrDefault() {
    return page == null ? DEFAULT_PAGE : page;
  }

  public int takeOrDefault() {
    return take == null ? DEFAULT_TAKE : take;
  }
}
