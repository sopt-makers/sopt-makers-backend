package org.sopt.makers.core.pagination;

import java.util.List;
import java.util.function.Function;

public record PageResult<T>(
    List<T> content,
    long totalElements,
    int totalPages,
    int page,
    int limit,
    boolean hasNext,
    boolean hasPrevious) {

  public PageResult {
    content = content == null ? List.of() : List.copyOf(content);
  }

  public <R> PageResult<R> map(Function<? super T, R> mapper) {
    return new PageResult<>(
        content.stream().map(mapper).toList(),
        totalElements,
        totalPages,
        page,
        limit,
        hasNext,
        hasPrevious);
  }
}
