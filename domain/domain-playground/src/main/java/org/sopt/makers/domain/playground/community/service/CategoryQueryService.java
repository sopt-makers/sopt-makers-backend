package org.sopt.makers.domain.playground.community.service;

import static org.sopt.makers.domain.playground.community.exception.CommunityFailure.NOT_FOUND_CATEGORY;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.community.Category;
import org.sopt.makers.domain.playground.community.CommunityCategoryCode;
import org.sopt.makers.domain.playground.community.exception.CommunityException;
import org.sopt.makers.domain.playground.community.port.CategoryRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryQueryService {

  private final CategoryRepositoryPort categoryRepositoryPort;

  public Category findActiveCategoryByCode(CommunityCategoryCode code) {
    return categoryRepositoryPort
        .findByCodeAndIsActiveTrue(code)
        .orElseThrow(() -> new CommunityException(NOT_FOUND_CATEGORY));
  }

  public List<Category> findActiveCategoriesByCodes(List<CommunityCategoryCode> codes) {
    return categoryRepositoryPort.findAllByCodeInAndIsActiveTrue(codes);
  }

  public List<Category> findAllActiveCategoriesWithParent() {
    return categoryRepositoryPort.findAllActiveWithParentOrderByDisplayOrderAsc();
  }
}
