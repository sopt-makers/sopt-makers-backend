package org.sopt.makers.domain.playground.member.profile.service;

import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.community.post.port.PostRepositoryPort;
import org.sopt.makers.domain.playground.member.profile.CoffeeChatStatus;
import org.sopt.makers.domain.playground.member.profile.UserProperties;
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
public class UserPropertyService {

  private static final String MAKERS_ACTIVITY_LABEL = "메이커스";

  private final PlaygroundProfileUserPort playgroundProfileUserPort;
  private final UserCareerRepositoryPort userCareerRepositoryPort;
  private final CoffeeChatActivationPort coffeeChatActivationPort;
  private final CoffeeChatHistoryPort coffeeChatHistoryPort;
  private final PostRepositoryPort postRepositoryPort;
  private final OfficialReviewCountPort officialReviewCountPort;

  public UserProperties getMemberProperties(Long userId) {
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

    return new UserProperties(
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

  // NOTE: 레거시는 is_coffee_chat_activate=true인 row만 확인하여 비활성화 유저도 NONE으로 응답하던 버그가 있었음.
  // 클라이언트(Web/App)가 NONE/ON 2가지 상태만 처리하도록 구현되어 있을 위험이 있어 레거시 응답 스펙을 의도적으로 유지함. (추후 클라이언트 대응 후 OFF 복원 필요)
  private CoffeeChatStatus resolveCoffeeChatStatus(Long userId) {
    return coffeeChatActivationPort.isCoffeeChatActive(userId) ? CoffeeChatStatus.ON : CoffeeChatStatus.NONE;
  }
}
