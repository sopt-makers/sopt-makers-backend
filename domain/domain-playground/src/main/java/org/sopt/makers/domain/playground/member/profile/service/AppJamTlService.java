package org.sopt.makers.domain.playground.member.profile.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.playground.member.ask.port.CurrentGenerationProvider;
import org.sopt.makers.domain.playground.member.profile.TlMemberCard;
import org.sopt.makers.domain.playground.member.profile.exception.UserProfileException;
import org.sopt.makers.domain.playground.member.profile.exception.UserProfileFailure;
import org.sopt.makers.domain.playground.member.tl.TlUser;
import org.sopt.makers.domain.playground.member.tl.port.TlUserRepositoryPort;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.port.PlaygroundProfileUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppJamTlService {

  private final PlaygroundProfileUserPort playgroundProfileUserPort;
  private final TlUserRepositoryPort tlUserRepositoryPort;
  private final CurrentGenerationProvider currentGenerationProvider;

  /** 최신 기수의 앱잼 TL로 참여한, 프로필이 있는 멤버들을 이름 오름차순으로 반환한다. */
  public List<TlMemberCard> getCurrentGenerationTlMembers(Long userId) {
    User requester = playgroundProfileUserPort.getUserWithActivities(userId);
    int currentGeneration = currentGenerationProvider.getCurrentGeneration();

    boolean isCurrentGenerationUser =
        requester.activities().activities().stream()
            .anyMatch(activity -> activity.isSopt() && activity.generation() == currentGeneration);
    if (!isCurrentGenerationUser) {
      throw new UserProfileException(UserProfileFailure.NOT_CURRENT_GENERATION_MEMBER);
    }

    List<TlUser> tlUsers = tlUserRepositoryPort.findAllByTlGeneration(currentGeneration);
    if (tlUsers.isEmpty()) {
      return List.of();
    }

    List<Long> tlMemberIds = tlUsers.stream().map(TlUser::memberUserId).toList();
    Map<Long, User> userById =
        playgroundProfileUserPort.findAllWithActivitiesByIds(tlMemberIds).stream()
            .filter(user -> !user.isFirstLogin())
            .collect(Collectors.toMap(User::id, Function.identity()));

    return tlUsers.stream()
        .filter(tlUser -> userById.containsKey(tlUser.memberUserId()))
        .map(tlUser -> new TlMemberCard(userById.get(tlUser.memberUserId()), tlUser))
        .sorted(Comparator.comparing(card -> card.user().profile().name()))
        .toList();
  }
}
