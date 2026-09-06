package org.sopt.makers.domain.user.adapter;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.user.Profile;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.UserCareer;
import org.sopt.makers.domain.user.UserFavor;
import org.sopt.makers.domain.user.UserLink;
import org.sopt.makers.domain.user.UserSearchCondition;
import org.sopt.makers.domain.user.UserSortType;
import org.sopt.makers.domain.user.WorkPreference;
import org.sopt.makers.domain.user.command.ActivityUpdateCommand;
import org.sopt.makers.domain.user.port.PlaygroundProfileUserPort;
import org.sopt.makers.domain.user.port.UserLinkRepositoryPort;
import org.sopt.makers.domain.user.service.UserCommandService;
import org.sopt.makers.domain.user.service.UserQueryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

/**
 * Playground 프로필 화면 전용 어댑터. {@link org.sopt.makers.domain.user.facade.UserFacade}는 전화번호 변경 시
 * 인증 검증을 강제하지만, 레거시 Playground `/api/v1/members/profile`은 이를 요구하지 않았으므로 여기서는
 * UserQueryService/UserCommandService를 직접 사용해 검증을 우회한다(Golden Rule 보존).
 */
@Component
@RequiredArgsConstructor
public class PlaygroundProfileUserAdapter implements PlaygroundProfileUserPort {

  private final UserQueryService userQueryService;
  private final UserCommandService userCommandService;
  private final UserLinkRepositoryPort userLinkRepositoryPort;

  @Override
  public Optional<User> findUser(Long userId) {
    return userQueryService.findById(userId);
  }

  @Override
  public User getUser(Long userId) {
    return userQueryService.getById(userId);
  }

  @Override
  public User getUserWithActivities(Long userId) {
    return userQueryService.getWithActivitiesById(userId);
  }

  @Override
  public List<User> findAllWithActivitiesByIds(List<Long> userIds) {
    return userQueryService.findAllWithActivitiesByIds(userIds);
  }

  @Override
  public List<User> searchUsersByName(String name, int limit) {
    return userQueryService
        .getUsersByCondition(
            new UserSearchCondition(null, null, name, null, null),
            PageRequest.of(0, limit),
            UserSortType.LATEST_GENERATION)
        .getContent();
  }

  @Override
  public List<Long> findCandidateUserIds(String mbti, Boolean employed) {
    return userQueryService.findAllUserIdsWithProfileByMbtiAndEmployed(mbti, employed);
  }

  @Override
  public void updateProfile(
      Long userId,
      String email,
      String phone,
      String profileImage,
      List<ActivityUpdateCommand> activityUpdates,
      String address,
      String university,
      String major,
      String introduction,
      String skill,
      String mbti,
      String mbtiDescription,
      Double sojuCapacity,
      String interest,
      UserFavor userFavor,
      String idealType,
      String selfIntroduction,
      Boolean allowOfficial,
      Boolean isPhoneBlind,
      WorkPreference workPreference,
      List<UserLink> links,
      List<UserCareer> careers) {
    User current = userQueryService.getWithActivitiesById(userId);
    Profile updated =
        current
            .profile()
            .update(
                email,
                phone,
                profileImage,
                address,
                university,
                major,
                introduction,
                skill,
                mbti,
                mbtiDescription,
                sojuCapacity,
                interest,
                userFavor,
                idealType,
                selfIntroduction,
                allowOfficial,
                isPhoneBlind,
                workPreference,
                links,
                careers);
    userCommandService.updateProfileWithActivities(userId, updated, activityUpdates);
  }

  @Override
  public void completeFirstLogin(Long userId) {
    userCommandService.completeFirstLogin(userId);
  }

  @Override
  public void upsertWorkPreference(Long userId, WorkPreference workPreference) {
    userCommandService.upsertWorkPreference(userId, workPreference);
  }

  @Override
  public Optional<UserLink> findLinkById(Long linkId) {
    return userLinkRepositoryPort.findById(linkId);
  }

  @Override
  public void deleteLinkById(Long linkId) {
    userLinkRepositoryPort.deleteById(linkId);
  }
}
