package org.sopt.makers.api.controller.crew.dining.dto.response;

import org.sopt.makers.domain.crew.dining.query.PageMetaDto;

public record DiningPageMetaResponse(
    int page,
    int take,
    int itemCount,
    int pageCount,
    boolean hasPreviousPage,
    boolean hasNextPage) {
  public static DiningPageMetaResponse from(PageMetaDto value) {
    return new DiningPageMetaResponse(
        value.getPage(),
        value.getTake(),
        value.getItemCount(),
        value.getPageCount(),
        value.isHasPreviousPage(),
        value.isHasNextPage());
  }
}
