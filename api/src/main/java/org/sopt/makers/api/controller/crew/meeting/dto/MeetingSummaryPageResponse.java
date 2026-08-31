package org.sopt.makers.api.controller.crew.meeting.dto;

import java.util.List;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.crew.meeting.facade.MeetingFacade;

public record MeetingSummaryPageResponse(
    List<MeetingSummaryResponse> data,
    long totalCount,
    int totalPage,
    int limit,
    int pageNo,
    boolean hasNextPage,
    boolean hasPrevPage) {

  public static MeetingSummaryPageResponse from(
      PageResult<MeetingFacade.MeetingSummaryResult> page) {
    return new MeetingSummaryPageResponse(
        page.content().stream().map(MeetingSummaryResponse::from).toList(),
        page.totalElements(),
        page.totalPages(),
        page.limit(),
        page.page(),
        page.hasNext(),
        page.hasPrevious());
  }
}
