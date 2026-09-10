package org.sopt.makers.domain.playground.member.profile;

import java.util.List;

/** Tier 1 캐시(top50 정렬 결과) 값. topUserIds는 최대 50건, totalCount는 필터 없는 전체 후보 수. */
public record UserProfileRanking(List<Long> topUserIds, int totalCount) {}
