package org.sopt.makers.api.controller.crew.meeting.dto;

import org.sopt.makers.core.pagination.PageResult;

public record MeetingPageMetaResponse(
    int page,
    int take,
    long itemCount,
    int pageCount,
    boolean hasPreviousPage,
    boolean hasNextPage) {

  public static MeetingPageMetaResponse from(PageResult<?> page) {
    return new MeetingPageMetaResponse(
        page.page(),
        page.limit(),
        page.totalElements(),
        page.totalPages(),
        page.hasPrevious(),
        page.hasNext());
  }
}
