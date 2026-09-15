package org.sopt.makers.domain.user.port;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.UserCareer;
import org.sopt.makers.domain.user.UserFavor;
import org.sopt.makers.domain.user.UserLink;
import org.sopt.makers.domain.user.WorkPreference;
import org.sopt.makers.domain.user.command.ActivityUpdateCommand;

public interface PlaygroundProfileUserPort {

  Optional<User> findUser(Long userId);

  User getUser(Long userId);

  User getUserWithActivities(Long userId);

  List<User> findAllWithActivitiesByIds(List<Long> userIds);

  /** 유저별 전체 링크 목록을 벌크 조회한다(id in ... 단일 쿼리). */
  Map<Long, List<UserLink>> findAllLinksByUserIds(List<Long> userIds);

  /** 유저별 전체 커리어 목록을 벌크 조회한다(id in ... 단일 쿼리). */
  Map<Long, List<UserCareer>> findAllCareersByUserIds(List<Long> userIds);

  /** 단건 프로필 상세 조회용 — 해당 유저의 전체 링크 목록 1건 조회. */
  List<UserLink> findLinksByUserId(Long userId);

  /** 단건 프로필 상세 조회용 — 해당 유저의 전체 커리어 목록 1건 조회. */
  List<UserCareer> findCareersByUserId(Long userId);

  /** 이름 검색, 최신 활동 기수 순 정렬, 최대 limit건. */
  List<User> searchUsersByName(String name, int limit);

  /** 프로필이 있는 유저 중 mbti/재직여부 DB 필터를 만족하는 id 목록. 각 조건이 null이면 미적용. */
  List<Long> findCandidateUserIds(String mbti, Boolean employed);

  /** Playground 프로필 저장/수정 전용 갱신 — 전화번호 인증 검증을 거치지 않는다(레거시 동작 보존). */
  void updateProfile(
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
      List<UserCareer> careers);

  void completeFirstLogin(Long userId);

  void upsertWorkPreference(Long userId, WorkPreference workPreference);

  Optional<UserLink> findLinkById(Long linkId);

  void deleteLinkById(Long linkId);
}
