package org.sopt.makers.api.controller.playground.community.dto;

import org.sopt.makers.domain.playground.community.Category;
import org.sopt.makers.domain.playground.community.CommunityCategoryCode;
import org.sopt.makers.domain.playground.community.CommunityCategoryGroup;

public record PostDetailCategoryResponse(
    CommunityCategoryGroup categoryGroup,
    CommunityCategoryCode code,
    String name,
    CommunityCategoryCode parentCode,
    String parentCategoryName) {

  public static PostDetailCategoryResponse from(Category category, Category parentCategory) {
    if (category == null) {
      return null;
    }
    return new PostDetailCategoryResponse(
        category.categoryGroup(),
        category.code(),
        category.name(),
        parentCategory == null ? null : parentCategory.code(),
        parentCategory == null ? null : parentCategory.name());
  }
}
