package org.sopt.makers.api.controller.crew.meeting.dto;

import java.util.List;
import org.sopt.makers.domain.crew.meeting.facade.MeetingFacade;
import org.springframework.data.domain.Page;

public record MeetingSummaryPageResponse(
    List<MeetingSummaryResponse> data,
    long totalCount,
    int totalPage,
    int limit,
    int pageNo,
    boolean hasNextPage,
    boolean hasPrevPage) {

  public static MeetingSummaryPageResponse from(
      Page<MeetingFacade.MeetingSummaryResult> page, int limit, int pageNo) {
    return new MeetingSummaryPageResponse(
        page.getContent().stream().map(MeetingSummaryResponse::from).toList(),
        page.getTotalElements(),
        page.getTotalPages(),
        limit,
        pageNo,
        page.hasNext(),
        page.hasPrevious());
  }
}
