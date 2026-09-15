package org.sopt.makers.api.controller.playground.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.sopt.makers.domain.playground.community.Category;
import org.sopt.makers.domain.playground.community.CommunityCategoryCode;
import org.sopt.makers.domain.playground.community.CommunityPostListFilter;

@Schema(description = "커뮤니티 카테고리")
public record CommunityCategoryResponse(
    @Schema(description = "카테고리 코드") String code,
    @Schema(description = "카테고리 이름") String name,
    @Schema(description = "카테고리 설명") String content,
    @Schema(description = "블라인드 가능 여부") Boolean hasBlind,
    @Schema(description = "하위 카테고리") List<CommunityCategoryResponse> children) {

  private static final Comparator<Category> CATEGORY_DISPLAY_ORDER_COMPARATOR =
      Comparator.comparing(Category::displayOrder, Comparator.nullsLast(Integer::compareTo))
          .thenComparing(Category::id);

  public static List<CommunityCategoryResponse> listFrom(List<Category> activeCategories) {
    Map<Long, List<Category>> childCategoriesByParentId =
        activeCategories.stream()
            .filter(category -> category.parentId() != null)
            .collect(Collectors.groupingBy(Category::parentId));

    return activeCategories.stream()
        .filter(category -> category.parentId() == null)
        .sorted(CATEGORY_DISPLAY_ORDER_COMPARATOR)
        .map(category -> CommunityCategoryResponse.from(category, childCategoriesByParentId))
        .toList();
  }

  private static CommunityCategoryResponse from(
      Category category, Map<Long, List<Category>> childCategoriesByParentId) {
    return new CommunityCategoryResponse(
        toResponseCode(category.code()),
        category.name(),
        category.content(),
        category.hasBlind(),
        toChildren(category, childCategoriesByParentId));
  }

  private static List<CommunityCategoryResponse> toChildren(
      Category category, Map<Long, List<Category>> childCategoriesByParentId) {
    return childCategoriesByParentId.getOrDefault(category.id(), List.of()).stream()
        .sorted(CATEGORY_DISPLAY_ORDER_COMPARATOR)
        .map(
            childCategory ->
                CommunityCategoryResponse.from(childCategory, childCategoriesByParentId))
        .toList();
  }

  private static String toResponseCode(CommunityCategoryCode code) {
    return switch (code) {
      case PROMOTION_EVENT -> CommunityPostListFilter.EVENT.name();
      case PROMOTION_PROJECT -> CommunityPostListFilter.PROJECT.name();
      case PROMOTION_RECRUIT -> CommunityPostListFilter.RECRUIT.name();
      case PROMOTION_ETC -> CommunityPostListFilter.ETC.name();

      case SOPTICLE_PLAN -> CommunityPostListFilter.PLAN.name();
      case SOPTICLE_DESIGN -> CommunityPostListFilter.DESIGN.name();
      case SOPTICLE_SERVER -> CommunityPostListFilter.SERVER.name();
      case SOPTICLE_WEB -> CommunityPostListFilter.WEB.name();
      case SOPTICLE_IOS -> CommunityPostListFilter.IOS.name();
      case SOPTICLE_ANDROID -> CommunityPostListFilter.ANDROID.name();
      case SOPTICLE_ETC -> CommunityPostListFilter.ETC.name();

      default -> code.name();
    };
  }
}
