package org.sopt.makers.domain.user.service;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.sopt.makers.domain.user.exception.UserFailure.NOT_FOUND_USER;
import static org.sopt.makers.domain.user.exception.UserFailure.NOT_FOUND_USER_ACTIVITY;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.ActivityList;
import org.sopt.makers.domain.user.Profile;
import org.sopt.makers.domain.user.SocialAccount;
import org.sopt.makers.domain.user.Team;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.exception.UserException;
import org.sopt.makers.domain.user.port.UserActivityHistoryRepositoryPort;
import org.sopt.makers.domain.user.port.UserCacheRepositoryPort;
import org.sopt.makers.domain.user.port.UserRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCommandService {

  private final UserRepositoryPort userRepositoryPort;
  private final UserActivityHistoryRepositoryPort activityRepositoryPort;
  private final UserCacheRepositoryPort userCacheRepositoryPort;

  public User createUser(SocialAccount socialAccount, Profile profile) {
    User newUser = User.createNewUser(socialAccount, profile);
    return userRepositoryPort.save(newUser);
  }

  /**
   * 프로필과 활동 이력을 하나의 트랜잭션에서 함께 수정한다.
   *
   * <p>전화번호 변경 여부에 대한 검증은 호출자(Facade) 책임이다.
   */
  public void updateProfileWithActivities(
      Long userId, Profile profile, List<ActivityUpdateCommand> activityUpdates) {
    User user =
        userRepositoryPort
            .findWithActivitiesById(userId)
            .orElseThrow(() -> new UserException(NOT_FOUND_USER));

    userRepositoryPort.save(user.updateProfile(profile));

    List<Activity> updated = applyActivityUpdates(user.activities(), activityUpdates);
    activityRepositoryPort.saveAll(userId, updated);

    userCacheRepositoryPort.evict(userId);
  }

  public User updateSocialAccount(Long userId, SocialAccount newAccount) {
    User user =
        userRepositoryPort.findById(userId).orElseThrow(() -> new UserException(NOT_FOUND_USER));
    return userRepositoryPort.save(user.updateSocialAccount(newAccount));
  }

  public void completeFirstLogin(Long userId) {
    User user =
        userRepositoryPort.findById(userId).orElseThrow(() -> new UserException(NOT_FOUND_USER));
    userRepositoryPort.save(user.completeFirstLogin());
  }

  public Activity addActivity(Long userId, Activity activity) {
    activity.validateActivityContents();
    return activityRepositoryPort.save(userId, activity);
  }

  private List<Activity> applyActivityUpdates(
      ActivityList existing, List<ActivityUpdateCommand> commands) {
    Map<Long, Activity> byId =
        existing.activities().stream().collect(toMap(Activity::id, identity()));

    return commands.stream()
        .map(
            cmd -> {
              Activity activity =
                  Optional.ofNullable(byId.get(cmd.activityId()))
                      .orElseThrow(() -> new UserException(NOT_FOUND_USER_ACTIVITY));
              return Activity.of(
                  cmd.activityId(),
                  activity.generation(),
                  cmd.team(),
                  activity.part(),
                  activity.role(),
                  activity.isSopt(),
                  activity.attendanceScore());
            })
        .toList();
  }

  /** 활동 이력 수정 커맨드. 기수/파트/역할은 고정이며 팀만 변경 가능하다. */
  public record ActivityUpdateCommand(Long activityId, Team team) {}
}
