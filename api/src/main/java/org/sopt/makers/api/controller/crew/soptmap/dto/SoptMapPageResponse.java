package org.sopt.makers.api.controller.crew.soptmap.dto;

import java.util.List;
import org.sopt.makers.core.pagination.PageResult;
import org.sopt.makers.domain.crew.soptmap.service.SoptMapService;

public record SoptMapPageResponse(List<SoptMapListItemResponse> soptMaps, PageMeta meta) {

  public static SoptMapPageResponse from(PageResult<SoptMapService.SoptMapView> page) {
    return new SoptMapPageResponse(
        page.content().stream().map(SoptMapListItemResponse::from).toList(),
        new PageMeta(
            page.page(),
            page.limit(),
            page.totalElements(),
            page.totalPages(),
            page.hasPrevious(),
            page.hasNext()));
  }

  public record PageMeta(
      int page,
      int take,
      long itemCount,
      int pageCount,
      boolean hasPreviousPage,
      boolean hasNextPage) {}
}
