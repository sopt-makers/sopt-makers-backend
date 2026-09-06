package org.sopt.makers.api.controller.crew.post.dto;

import org.sopt.makers.core.pagination.PageResult;

public record PostPageMetaResponse(
    int page,
    int take,
    long itemCount,
    int pageCount,
    boolean hasPreviousPage,
    boolean hasNextPage) {

  public static PostPageMetaResponse from(PageResult<?> page) {
    return new PostPageMetaResponse(
        page.page(),
        page.limit(),
        page.totalElements(),
        page.totalPages(),
        page.hasPrevious(),
        page.hasNext());
  }
}
