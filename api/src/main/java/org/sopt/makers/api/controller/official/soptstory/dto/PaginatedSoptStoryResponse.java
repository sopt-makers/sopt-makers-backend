package org.sopt.makers.api.controller.official.soptstory.dto;

import java.util.List;

public record PaginatedSoptStoryResponse(
    List<SoptStoryResponse> content,
    int totalCount,
    int pageSize,
    int currentPage,
    int totalPages,
    boolean hasNext,
    boolean hasPrevious) {

  public static PaginatedSoptStoryResponse of(
      List<SoptStoryResponse> content, int totalCount, int pageSize, int currentPage) {
    int totalPages = (int) Math.ceil((double) totalCount / pageSize);
    return new PaginatedSoptStoryResponse(
        content,
        totalCount,
        pageSize,
        currentPage,
        totalPages,
        currentPage < totalPages,
        currentPage > 1);
  }
}
