package org.sopt.makers.domain.playground.member.profile.service;

import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.community.post.port.PostRepositoryPort;
import org.sopt.makers.domain.playground.member.profile.CoffeeChatStatus;
import org.sopt.makers.domain.playground.member.profile.MemberProperties;
import org.sopt.makers.domain.playground.member.profile.port.CoffeeChatActivationPort;
import org.sopt.makers.domain.playground.member.profile.port.CoffeeChatHistoryPort;
import org.sopt.makers.domain.playground.member.profile.port.OfficialReviewCountPort;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.UserCareer;
import org.sopt.makers.domain.user.port.PlaygroundProfileUserPort;
import org.sopt.makers.domain.user.port.UserCareerRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Amplitude 등 분석 도구용 유저 프로퍼티 및 활동 통계 집계 조회 유스케이스. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberPropertyService {

  private static final String MAKERS_ACTIVITY_LABEL = "메이커스";

  private final PlaygroundProfileUserPort playgroundProfileUserPort;
  private final UserCareerRepositoryPort userCareerRepositoryPort;
  private final CoffeeChatActivationPort coffeeChatActivationPort;
  private final CoffeeChatHistoryPort coffeeChatHistoryPort;
  private final PostRepositoryPort postRepositoryPort;
  private final OfficialReviewCountPort officialReviewCountPort;

  public MemberProperties getMemberProperties(Long userId) {
    User user = playgroundProfileUserPort.getUserWithActivities(userId);
    UserCareer lastCareer =
        userCareerRepositoryPort.findLastCareersByUserIds(List.of(userId)).stream()
            .findFirst()
            .orElse(null);

    String major = lastCareer == null ? user.profile().major() : null;
    String job = lastCareer != null ? lastCareer.title() : null;
    String organization = lastCareer != null ? lastCareer.companyName() : user.profile().university();

    List<Activity> sortedActivities =
        user.activities().activities().stream()
            .sorted(Comparator.comparingInt(Activity::generation).thenComparing(a -> !a.isSopt()))
            .toList();
    List<Integer> generations = sortedActivities.stream().map(Activity::generation).toList();
    List<String> parts =
        sortedActivities.stream()
            .map(a -> a.isSopt() ? a.part().getName() : MAKERS_ACTIVITY_LABEL)
            .toList();

    CoffeeChatStatus coffeeChatStatus = resolveCoffeeChatStatus(userId);
    long receivedCoffeeChatCount = coffeeChatHistoryPort.countReceivedBy(userId);
    long sentCoffeeChatCount = coffeeChatHistoryPort.countSentBy(userId);
    long uploadSopticleCount = postRepositoryPort.countSopticleByWriterId(userId);
    int uploadReviewCount = officialReviewCountPort.countReviewsByAuthor(user.profile().name());

    return new MemberProperties(
        userId,
        major,
        job,
        organization,
        parts,
        generations,
        coffeeChatStatus,
        receivedCoffeeChatCount,
        sentCoffeeChatCount,
        uploadSopticleCount,
        (long) uploadReviewCount);
  }

  private CoffeeChatStatus resolveCoffeeChatStatus(Long userId) {
    if (!coffeeChatActivationPort.existsCoffeeChat(userId)) {
      return CoffeeChatStatus.NONE;
    }
    return coffeeChatActivationPort.isCoffeeChatActive(userId) ? CoffeeChatStatus.ON : CoffeeChatStatus.OFF;
  }
}
