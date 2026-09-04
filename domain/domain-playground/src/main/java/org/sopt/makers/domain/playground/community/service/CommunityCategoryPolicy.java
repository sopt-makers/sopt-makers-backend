package org.sopt.makers.domain.playground.community.service;

import static org.sopt.makers.domain.playground.community.exception.CommunityFailure.INVALID_FREE_FILTER;
import static org.sopt.makers.domain.playground.community.exception.CommunityFailure.INVALID_PROMOTION_FILTER;
import static org.sopt.makers.domain.playground.community.exception.CommunityFailure.INVALID_SOPTICLE_FILTER;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.sopt.makers.domain.playground.community.CommunityCategoryCode;
import org.sopt.makers.domain.playground.community.CommunityCategoryGroup;
import org.sopt.makers.domain.playground.community.CommunityPostListCategory;
import org.sopt.makers.domain.playground.community.CommunityPostListFilter;
import org.sopt.makers.domain.playground.community.CommunityPostTag;
import org.sopt.makers.domain.playground.community.exception.CommunityException;
import org.springframework.stereotype.Component;

@Component
public class CommunityCategoryPolicy {

  private static final List<CommunityCategoryCode> PROMOTION_ALL_CODES =
      List.of(
          CommunityCategoryCode.PROMOTION,
          CommunityCategoryCode.PROMOTION_EVENT,
          CommunityCategoryCode.PROMOTION_PROJECT,
          CommunityCategoryCode.PROMOTION_RECRUIT,
          CommunityCategoryCode.PROMOTION_ETC);

  private static final List<CommunityCategoryCode> SOPTICLE_ALL_CODES =
      List.of(
          CommunityCategoryCode.SOPTICLE,
          CommunityCategoryCode.SOPTICLE_PLAN,
          CommunityCategoryCode.SOPTICLE_DESIGN,
          CommunityCategoryCode.SOPTICLE_SERVER,
          CommunityCategoryCode.SOPTICLE_WEB,
          CommunityCategoryCode.SOPTICLE_IOS,
          CommunityCategoryCode.SOPTICLE_ANDROID,
          CommunityCategoryCode.SOPTICLE_ETC);

  private static final Map<CommunityPostListFilter, CommunityCategoryCode>
      PROMOTION_FILTER_CODE_MAP =
          Map.of(
              CommunityPostListFilter.EVENT, CommunityCategoryCode.PROMOTION_EVENT,
              CommunityPostListFilter.PROJECT, CommunityCategoryCode.PROMOTION_PROJECT,
              CommunityPostListFilter.RECRUIT, CommunityCategoryCode.PROMOTION_RECRUIT,
              CommunityPostListFilter.ETC, CommunityCategoryCode.PROMOTION_ETC);

  private static final Map<CommunityPostListFilter, CommunityCategoryCode>
      SOPTICLE_FILTER_CODE_MAP =
          Map.of(
              CommunityPostListFilter.PLAN, CommunityCategoryCode.SOPTICLE_PLAN,
              CommunityPostListFilter.DESIGN, CommunityCategoryCode.SOPTICLE_DESIGN,
              CommunityPostListFilter.SERVER, CommunityCategoryCode.SOPTICLE_SERVER,
              CommunityPostListFilter.WEB, CommunityCategoryCode.SOPTICLE_WEB,
              CommunityPostListFilter.IOS, CommunityCategoryCode.SOPTICLE_IOS,
              CommunityPostListFilter.ANDROID, CommunityCategoryCode.SOPTICLE_ANDROID,
              CommunityPostListFilter.ETC, CommunityCategoryCode.SOPTICLE_ETC);

  private static final Set<CommunityPostListFilter> PROMOTION_FILTERS =
      Set.of(
          CommunityPostListFilter.ALL,
          CommunityPostListFilter.EVENT,
          CommunityPostListFilter.PROJECT,
          CommunityPostListFilter.RECRUIT,
          CommunityPostListFilter.ETC);

  private static final Set<CommunityPostListFilter> SOPTICLE_FILTERS =
      Set.of(
          CommunityPostListFilter.ALL,
          CommunityPostListFilter.PLAN,
          CommunityPostListFilter.DESIGN,
          CommunityPostListFilter.SERVER,
          CommunityPostListFilter.WEB,
          CommunityPostListFilter.IOS,
          CommunityPostListFilter.ANDROID,
          CommunityPostListFilter.ETC);

  /**
   * Non-breaking Phase 1 진입점. 신규 단일 코드(categoryCode)가 있으면 이를 우선 사용하고, 없으면 기존
   * category+filter 조합으로 폴백한다.
   */
  public List<CommunityCategoryCode> resolveCategoryCodes(
      CommunityCategoryCode categoryCode,
      CommunityPostListCategory category,
      CommunityPostListFilter filter) {
    if (categoryCode != null) {
      return resolveCategoryCodes(categoryCode);
    }

    return resolveCategoryCodes(category, filter);
  }

  /** 신규 단일 코드를 조회 대상 코드 목록으로 변환한다. 그룹 대표 코드(PROMOTION, SOPTICLE)는 하위 코드 전체로 확장한다. */
  public List<CommunityCategoryCode> resolveCategoryCodes(CommunityCategoryCode categoryCode) {
    if (categoryCode == CommunityCategoryCode.PROMOTION) {
      return PROMOTION_ALL_CODES;
    }

    if (categoryCode == CommunityCategoryCode.SOPTICLE) {
      return SOPTICLE_ALL_CODES;
    }

    return List.of(categoryCode);
  }

  /** 기존 category+filter 조합 조회 로직(fallback). */
  public List<CommunityCategoryCode> resolveCategoryCodes(
      CommunityPostListCategory category, CommunityPostListFilter filter) {
    return switch (category) {
      case FREE -> resolveFreeCodes(filter);
      case PROMOTION -> resolvePromotionCodes(normalizeFilter(filter));
      case SOPTICLE -> resolveSopticleCodes(normalizeFilter(filter));
    };
  }

  public boolean isSopticleCategoryCode(CommunityCategoryCode categoryCode) {
    return SOPTICLE_ALL_CODES.contains(categoryCode);
  }

  public List<CommunityPostListFilter> getAvailableFilters(CommunityPostListCategory category) {
    return switch (category) {
      case FREE -> List.of();
      case PROMOTION ->
          List.of(
              CommunityPostListFilter.ALL,
              CommunityPostListFilter.EVENT,
              CommunityPostListFilter.PROJECT,
              CommunityPostListFilter.RECRUIT,
              CommunityPostListFilter.ETC);
      case SOPTICLE ->
          List.of(
              CommunityPostListFilter.ALL,
              CommunityPostListFilter.PLAN,
              CommunityPostListFilter.DESIGN,
              CommunityPostListFilter.SERVER,
              CommunityPostListFilter.WEB,
              CommunityPostListFilter.IOS,
              CommunityPostListFilter.ANDROID,
              CommunityPostListFilter.ETC);
    };
  }

  /**
   * JPA Category 엔티티 대신 categoryGroup/code 값만으로 미리보기 태그를 판단한다. (domain 모듈은 storage의 JPA
   * Entity에 의존할 수 없다.)
   */
  public CommunityPostTag resolvePreviewTag(
      CommunityCategoryGroup categoryGroup, CommunityCategoryCode code) {
    if (categoryGroup == null) {
      return null;
    }

    if (categoryGroup == CommunityCategoryGroup.FREE) {
      return CommunityPostTag.FREE;
    }

    if (categoryGroup == CommunityCategoryGroup.SOPTICLE) {
      return CommunityPostTag.SOPTICLE;
    }

    if (categoryGroup == CommunityCategoryGroup.PROMOTION) {
      return resolvePromotionPreviewTag(code);
    }

    return null;
  }

  private CommunityPostTag resolvePromotionPreviewTag(CommunityCategoryCode code) {
    if (code == null) {
      return CommunityPostTag.PROMOTION;
    }

    return switch (code) {
      case PROMOTION_EVENT -> CommunityPostTag.EVENT;
      case PROMOTION_PROJECT -> CommunityPostTag.PROJECT;
      case PROMOTION_RECRUIT -> CommunityPostTag.RECRUIT;
      case PROMOTION, PROMOTION_ETC -> CommunityPostTag.PROMOTION;
      default -> CommunityPostTag.PROMOTION;
    };
  }

  private List<CommunityCategoryCode> resolveFreeCodes(CommunityPostListFilter filter) {
    if (filter != null) {
      throw new CommunityException(INVALID_FREE_FILTER);
    }

    return List.of(CommunityCategoryCode.FREE);
  }

  private List<CommunityCategoryCode> resolvePromotionCodes(CommunityPostListFilter filter) {
    if (!PROMOTION_FILTERS.contains(filter)) {
      throw new CommunityException(INVALID_PROMOTION_FILTER);
    }

    if (filter == CommunityPostListFilter.ALL) {
      return PROMOTION_ALL_CODES;
    }

    return List.of(PROMOTION_FILTER_CODE_MAP.get(filter));
  }

  private List<CommunityCategoryCode> resolveSopticleCodes(CommunityPostListFilter filter) {
    if (!SOPTICLE_FILTERS.contains(filter)) {
      throw new CommunityException(INVALID_SOPTICLE_FILTER);
    }

    if (filter == CommunityPostListFilter.ALL) {
      return SOPTICLE_ALL_CODES;
    }

    return List.of(SOPTICLE_FILTER_CODE_MAP.get(filter));
  }

  private CommunityPostListFilter normalizeFilter(CommunityPostListFilter filter) {
    return filter == null ? CommunityPostListFilter.ALL : filter;
  }
}
