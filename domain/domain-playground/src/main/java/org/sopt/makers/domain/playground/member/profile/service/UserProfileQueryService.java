package org.sopt.makers.domain.playground.member.profile.service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.sopt.makers.domain.playground.member.ask.port.CurrentGenerationProvider;
import org.sopt.makers.domain.playground.member.ask.service.UserAskQueryService;
import org.sopt.makers.domain.playground.member.profile.AppJamObMemberIds;
import org.sopt.makers.domain.playground.member.profile.MakersMemberIds;
import org.sopt.makers.domain.playground.member.profile.MakersUserProfile;
import org.sopt.makers.domain.playground.member.profile.UserInfo;
import org.sopt.makers.domain.playground.member.profile.UserProfileDetail;
import org.sopt.makers.domain.playground.member.profile.UserSummary;
import org.sopt.makers.domain.playground.member.profile.exception.UserProfileException;
import org.sopt.makers.domain.playground.member.profile.exception.UserProfileFailure;
import org.sopt.makers.domain.playground.member.profile.port.CoffeeChatActivationPort;
import org.sopt.makers.domain.playground.member.profile.port.PlaygroundProjectRelationPort;
import org.sopt.makers.domain.playground.member.profile.port.UserActivityCheckPort;
import org.sopt.makers.domain.playground.project.Project;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.port.PlaygroundProfileUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProfileQueryService {

  private static final int SEARCH_LIMIT = 30;

  private final PlaygroundProfileUserPort playgroundProfileUserPort;
  private final PlaygroundProjectRelationPort projectRelationPort;
  private final CoffeeChatActivationPort coffeeChatActivationPort;
  private final UserAskQueryService userAskQueryService;
  private final CurrentGenerationProvider currentGenerationProvider;
  private final UserActivityCheckPort userActivityCheckPort;

  public UserSummary getMemberSummary(Long id) {
    User user = playgroundProfileUserPort.getUserWithActivities(id);
    return toSummary(user);
  }

  public User getMemberUser(Long userId) {
    return playgroundProfileUserPort.getUser(userId);
  }

  public boolean isCoffeeChatActive(Long userId) {
    return coffeeChatActivationPort.isCoffeeChatActive(userId);
  }

  public UserInfo getMyInfo(Long userId) {
    User user = playgroundProfileUserPort.getUserWithActivities(userId);
    UserSummary summary = toSummary(user);

    boolean hasCoffeeChat = coffeeChatActivationPort.isCoffeeChatActive(userId);
    boolean hasWorkPreference = user.profile().workPreference() != null;
    boolean enableWorkPreferenceEvent =
        (summary.generation() != null
                && Objects.equals(summary.generation(), currentGenerationProvider.getCurrentGeneration()))
            || AppJamObMemberIds.IDS.contains(userId);

    return new UserInfo(summary, hasCoffeeChat, hasWorkPreference, enableWorkPreferenceEvent);
  }

  public List<UserSummary> searchByName(String name) {
    return playgroundProfileUserPort.searchUsersByName(name, SEARCH_LIMIT).stream()
        .map(this::toSummary)
        .toList();
  }

  public List<MakersUserProfile> getMakersProfiles() {
    List<User> users = playgroundProfileUserPort.findAllWithActivitiesByIds(MakersMemberIds.IDS);
    return users.stream()
        .filter(user -> !user.isFirstLogin())
        .map(
            user ->
                new MakersUserProfile(
                    user.id(),
                    user.profile().name(),
                    user.profile().profileImage(),
                    sortActivities(user.activities().activities()),
                    user.profile().careers()))
        .toList();
  }

  public UserProfileDetail getProfileDetail(Long profileId, Long viewerId) {
    User user = playgroundProfileUserPort.getUserWithActivities(profileId);
    if (user.isFirstLogin()) {
      throw new UserProfileException(UserProfileFailure.NOT_FOUND_PROFILE);
    }
    if (user.activities().activities().isEmpty()) {
      throw new UserProfileException(UserProfileFailure.NOT_FOUND_LEGACY_GENERATION_MEMBER);
    }

    boolean isMine = Objects.equals(profileId, viewerId);
    List<Project> projects = projectRelationPort.findProjectsByUserId(profileId);
    boolean isCoffeeChatActivate = coffeeChatActivationPort.isCoffeeChatActive(profileId);
    boolean hasRecentQuestion = userAskQueryService.hasRecentAsk(profileId);

    return new UserProfileDetail(user, isMine, isCoffeeChatActivate, hasRecentQuestion, projects);
  }

  private UserSummary toSummary(User user) {
    List<Activity> activities = user.activities().activities();
    Integer generation =
        activities.isEmpty()
            ? null
            : activities.stream().map(Activity::generation).max(Integer::compareTo).orElse(null);
    boolean hasProfile = !user.isFirstLogin();
    boolean editActivitiesAble = userActivityCheckPort.isEditActivitiesAble(user.id());

    return new UserSummary(
        user.id(), user.profile().name(), generation, user.profile().profileImage(), hasProfile,
        editActivitiesAble);
  }

  private List<Activity> sortActivities(List<Activity> activities) {
    return activities.stream()
        .sorted(Comparator.comparingInt(Activity::generation).thenComparing(a -> !a.isSopt()))
        .toList();
  }
}
