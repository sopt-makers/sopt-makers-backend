package org.sopt.makers.storage.db.common;

import java.util.function.Function;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.sopt.makers.core.pagination.PageQuery;
import org.sopt.makers.core.pagination.PageResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PageMapper {

  public static Pageable toPageable(PageQuery pageQuery) {
    return PageRequest.of(pageQuery.page() - 1, pageQuery.limit());
  }

  public static Pageable toPageable(PageQuery pageQuery, Sort sort) {
    return PageRequest.of(pageQuery.page() - 1, pageQuery.limit(), sort);
  }

  public static <E, D> PageResult<D> toPageResult(Page<E> page, Function<? super E, D> mapper) {
    return new PageResult<>(
        page.getContent().stream().map(mapper).toList(),
        page.getTotalElements(),
        page.getTotalPages(),
        page.getNumber() + 1,
        page.getSize(),
        page.hasNext(),
        page.hasPrevious());
  }
}
