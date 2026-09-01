package org.sopt.makers.api.controller.crew.meeting.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.sopt.makers.domain.crew.meeting.MeetingCategory;
import org.sopt.makers.domain.crew.meeting.MeetingStatus;
import org.sopt.makers.domain.crew.meeting.service.MeetingService;

public record GetMeetingsRequest(
    @Min(1) Integer pageNo,
    @Min(1) @Max(100) Integer limit,
    @Size(max = 100) String search,
    String category,
    Integer status) {

  private static final int DEFAULT_PAGE_NO = 1;
  private static final int DEFAULT_LIMIT = 10;

  public int pageNoOrDefault() {
    return pageNo == null ? DEFAULT_PAGE_NO : pageNo;
  }

  public int limitOrDefault() {
    return limit == null ? DEFAULT_LIMIT : limit;
  }

  public MeetingService.SearchMeetingsCommand toCommand() {
    return new MeetingService.SearchMeetingsCommand(
        search,
        category == null || category.isBlank() ? null : MeetingCategory.ofValue(category),
        status == null ? null : MeetingStatus.ofValue(status));
  }
}
