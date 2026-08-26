package org.sopt.makers.core.pagination;

public record PageQuery(int page, int limit) {

  public PageQuery {
    if (page < 1) {
      throw new IllegalArgumentException("page는 1 이상이어야 합니다.");
    }
    if (limit < 1) {
      throw new IllegalArgumentException("limit은 1 이상이어야 합니다.");
    }
  }
}
