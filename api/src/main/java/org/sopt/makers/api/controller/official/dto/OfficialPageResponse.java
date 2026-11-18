package org.sopt.makers.api.controller.official.dto;

import java.util.List;

/**
 * 공식 홈페이지 활동 후기·프로젝트 목록의 페이지 응답.
 *
 * <p>공홈 프론트가 레거시(sopt.org-backend-spring) {@code PaginateResponseDto}와 같은 필드를 읽으므로 필드 이름과 계산 방식을
 * 그대로 따른다. 레거시에서 이 형식을 쓰던 후기·프로젝트 목록 전용이며, 솝트스토리처럼 다른 형식을 쓰던 목록에는 쓰지 않는다.
 */
public record OfficialPageResponse<T>(
    List<T> data,
    int totalCount,
    int limit,
    int currentPage,
    int totalPage,
    boolean hasNextPage,
    boolean hasPrevPage) {

  public static <T> OfficialPageResponse<T> of(
      List<T> data, int totalCount, int limit, int currentPage) {
    int totalPage = (int) Math.ceil((double) totalCount / limit);
    return new OfficialPageResponse<>(
        data, totalCount, limit, currentPage, totalPage, totalPage > currentPage, currentPage > 1);
  }
}
