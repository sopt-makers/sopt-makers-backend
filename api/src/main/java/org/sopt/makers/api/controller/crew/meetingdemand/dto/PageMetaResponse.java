package org.sopt.makers.api.controller.crew.meetingdemand.dto;

import org.sopt.makers.core.pagination.PageResult;

public record PageMetaResponse(
    int page,
    int take,
    long itemCount,
    int pageCount,
    boolean hasPreviousPage,
    boolean hasNextPage) {

  public static PageMetaResponse from(PageResult<?> page) {
    return new PageMetaResponse(
        page.page(),
        page.limit(),
        page.totalElements(),
        page.totalPages(),
        page.hasPrevious(),
        page.hasNext());
  }
}
