package org.sopt.makers.api.controller.official.soptstory.dto;

import jakarta.validation.constraints.Min;

public record GetSoptStoryListRequest(String sort, @Min(1) Integer pageNo, @Min(1) Integer limit) {

  public GetSoptStoryListRequest {
    if (sort == null || sort.isBlank()) {
      sort = "recent";
    }
  }

  private static final int DEFAULT_PAGE_NO = 1;
  private static final int DEFAULT_LIMIT = 10;

  public int pageNoOrDefault() {
    return pageNo == null ? DEFAULT_PAGE_NO : pageNo;
  }

  public int limitOrDefault() {
    return limit == null ? DEFAULT_LIMIT : limit;
  }
}
