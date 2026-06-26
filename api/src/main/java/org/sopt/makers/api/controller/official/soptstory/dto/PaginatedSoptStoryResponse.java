package org.sopt.makers.api.controller.official.soptstory.dto;

import java.util.List;

public record PaginatedSoptStoryResponse(
    List<SoptStoryResponse> data, int totalCount, int limit, int pageNo) {

  public static PaginatedSoptStoryResponse of(
      List<SoptStoryResponse> data, int totalCount, int limit, int pageNo) {
    return new PaginatedSoptStoryResponse(data, totalCount, limit, pageNo);
  }
}
