package org.sopt.makers.api.controller.official.common.dto;

import java.util.List;

/**
 * 공식 홈페이지 목록 응답의 페이지 정보.
 *
 * <p>공홈 프론트가 레거시(sopt.org-backend-spring) {@code PaginateResponseDto}와 같은 필드를 읽으므로 이름과 계산 방식을 그대로
 * 따른다. {@code pageNo}는 기존 클라이언트 호환을 위해 {@code currentPage}와 같은 값으로 유지한다.
 */
public record OfficialPageResponse<T>(
    List<T> data,
    int totalCount,
    int limit,
    int pageNo,
    int currentPage,
    int totalPage,
    boolean hasNextPage,
    boolean hasPrevPage) {

  public static <T> OfficialPageResponse<T> of(
      List<T> data, int totalCount, int limit, int pageNo) {
    int totalPage = (int) Math.ceil((double) totalCount / limit);
    return new OfficialPageResponse<>(
        data, totalCount, limit, pageNo, pageNo, totalPage, totalPage > pageNo, pageNo > 1);
  }
}
