package org.sopt.makers.api.controller.crew.meetingdemand.dto;

import org.springframework.data.domain.Page;

public record PageMetaResponse(
    int page,
    int take,
    long itemCount,
    int pageCount,
    boolean hasPreviousPage,
    boolean hasNextPage) {

  public static PageMetaResponse from(Page<?> page) {
    return new PageMetaResponse(
        page.getNumber() + 1,
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages(),
        page.hasPrevious(),
        page.hasNext());
  }
}
