package org.sopt.makers.storage.db.playground.community.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.community.Category;
import org.sopt.makers.domain.playground.community.CommunityCategoryCode;
import org.sopt.makers.domain.playground.community.port.CategoryRepositoryPort;
import org.sopt.makers.storage.db.playground.community.entity.CategoryEntity;
import org.sopt.makers.storage.db.playground.community.repository.CategoryJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryRepositoryAdapter implements CategoryRepositoryPort {

  private final CategoryJpaRepository categoryJpaRepository;

  @Override
  public Optional<Category> findByCodeAndIsActiveTrue(CommunityCategoryCode code) {
    return categoryJpaRepository.findByCodeAndIsActiveTrue(code).map(CategoryEntity::toDomain);
  }

  @Override
  public List<Category> findAllByCodeInAndIsActiveTrue(List<CommunityCategoryCode> codes) {
    return categoryJpaRepository.findAllByCodeInAndIsActiveTrue(codes).stream()
        .map(CategoryEntity::toDomain)
        .toList();
  }

  @Override
  public List<Category> findAllActiveWithParentOrderByDisplayOrderAsc() {
    return categoryJpaRepository.findAllActiveWithParentOrderByDisplayOrderAsc().stream()
        .map(CategoryEntity::toDomain)
        .toList();
  }
}
