package org.sopt.makers.domain.playground.community.port;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.playground.community.Category;
import org.sopt.makers.domain.playground.community.CommunityCategoryCode;

public interface CategoryRepositoryPort {

  Optional<Category> findByCodeAndIsActiveTrue(CommunityCategoryCode code);

  List<Category> findAllByCodeInAndIsActiveTrue(List<CommunityCategoryCode> codes);

  List<Category> findAllActiveWithParentOrderByDisplayOrderAsc();

  Optional<Category> findById(Long id);

  List<Category> findAllByIds(List<Long> ids);
}
