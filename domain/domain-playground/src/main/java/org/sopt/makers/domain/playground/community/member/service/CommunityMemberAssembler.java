package org.sopt.makers.domain.playground.community.member.service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.community.member.CommunityMemberSummary;
import org.sopt.makers.domain.playground.community.member.port.CommunityMemberPort;
import org.sopt.makers.domain.playground.community.member.port.CommunityMemberPort.ActivityInfo;
import org.sopt.makers.domain.playground.community.member.port.CommunityMemberPort.MemberInfo;
import org.springframework.stereotype.Component;

/** 게시글 작성자 표시 정보(MemberVo에 대응)를 조립한다. */
@Component
@RequiredArgsConstructor
public class CommunityMemberAssembler {

  private static final String MAKERS_DISPLAY_PART = "메이커스";

  private final CommunityMemberPort communityMemberPort;

  public Map<Long, CommunityMemberSummary> getMemberSummaryMap(List<Long> memberIds) {
    if (memberIds == null || memberIds.isEmpty()) {
      return Map.of();
    }

    List<Long> distinctMemberIds = memberIds.stream().filter(Objects::nonNull).distinct().toList();

    if (distinctMemberIds.isEmpty()) {
      return Map.of();
    }

    Map<Long, CommunityMemberSummary> summaryMap = new LinkedHashMap<>();

    for (MemberInfo memberInfo : communityMemberPort.findMemberInfosByIds(distinctMemberIds)) {
      summaryMap.put(memberInfo.id(), toSummary(memberInfo));
    }

    return summaryMap;
  }

  public CommunityMemberSummary getMemberSummary(Long memberId) {
    if (memberId == null) {
      return null;
    }
    return getMemberSummaryMap(List.of(memberId)).get(memberId);
  }

  private CommunityMemberSummary toSummary(MemberInfo memberInfo) {
    ActivityInfo latestActivity = pickLatestActivity(memberInfo.activities());

    CommunityMemberSummary.Activity activity =
        latestActivity == null
            ? null
            : new CommunityMemberSummary.Activity(
                latestActivity.generation(),
                latestActivity.isSopt() ? latestActivity.part() : MAKERS_DISPLAY_PART,
                latestActivity.team());

    CommunityMemberSummary.Career career =
        memberInfo.lastCareer() == null
            ? null
            : new CommunityMemberSummary.Career(
                memberInfo.lastCareer().companyName(), memberInfo.lastCareer().title());

    return new CommunityMemberSummary(
        memberInfo.id(), memberInfo.name(), memberInfo.profileImage(), activity, career);
  }

  /** 같은 기수라면 SOPT(isSopt=true) 활동을 우선한다. 메이커스 1~4기는 SOPT 31~34기로 정규화하여 비교한다. */
  private ActivityInfo pickLatestActivity(List<ActivityInfo> activities) {
    if (activities == null || activities.isEmpty()) {
      return null;
    }

    return activities.stream()
        .max(
            Comparator.comparingInt(this::normalizedGeneration)
                .thenComparing(ActivityInfo::isSopt))
        .orElse(null);
  }

  private int normalizedGeneration(ActivityInfo activity) {
    if (!activity.isSopt() && activity.generation() >= 1 && activity.generation() <= 4) {
      return activity.generation() + 30;
    }
    return activity.generation();
  }
}
