package org.sopt.makers.storage.db.playground.community.repository;

import java.util.List;
import java.util.Optional;
import org.sopt.makers.domain.playground.community.CommunityCategoryCode;
import org.sopt.makers.storage.db.playground.community.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, Long> {

  Optional<CategoryEntity> findByCodeAndIsActiveTrue(CommunityCategoryCode code);

  List<CategoryEntity> findAllByCodeInAndIsActiveTrue(List<CommunityCategoryCode> codes);

  @Query(
      """
      select category
      from CategoryEntity category
      left join fetch category.parent
      where category.isActive = true
      order by category.displayOrder asc, category.id asc
      """)
  List<CategoryEntity> findAllActiveWithParentOrderByDisplayOrderAsc();
}
