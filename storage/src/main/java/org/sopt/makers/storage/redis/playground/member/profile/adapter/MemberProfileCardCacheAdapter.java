package org.sopt.makers.storage.redis.playground.member.profile.adapter;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.playground.member.profile.port.UserProfileCardCachePort;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.ActivityList;
import org.sopt.makers.domain.user.Profile;
import org.sopt.makers.domain.user.Role;
import org.sopt.makers.domain.user.Team;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.UserCareer;
import org.sopt.makers.domain.user.UserFavor;
import org.sopt.makers.domain.user.UserLink;
import org.sopt.makers.storage.redis.playground.member.profile.cache.CachedMemberProfileCard;
import org.sopt.makers.storage.redis.playground.member.profile.cache.CachedMemberProfileCard.CachedMemberCareer;
import org.sopt.makers.storage.redis.playground.member.profile.cache.CachedMemberProfileCard.CachedMemberLink;
import org.sopt.makers.storage.redis.user.cache.CachedUserActivity;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/** Tier 2 캐시 어댑터. Redis 장애/역직렬화 실패 시 조용히 미스로 취급해 호출부가 DB 경로로 폴백하게 한다. */
@Slf4j
@Component
@RequiredArgsConstructor
public class MemberProfileCardCacheAdapter implements UserProfileCardCachePort {

  private static final String KEY_PREFIX = "member:card:";
  private static final Duration TTL = Duration.ofMinutes(40);

  private final RedisTemplate<String, CachedMemberProfileCard> memberProfileCardRedisTemplate;

  @Override
  public Map<Long, User> getAllPresent(Collection<Long> userIds) {
    if (userIds.isEmpty()) {
      return Map.of();
    }
    try {
      List<Long> ids = List.copyOf(userIds);
      List<String> keys = ids.stream().map(id -> KEY_PREFIX + id).toList();
      List<CachedMemberProfileCard> values = memberProfileCardRedisTemplate.opsForValue().multiGet(keys);

      Map<Long, User> result = new HashMap<>();
      if (values != null) {
        for (int i = 0; i < ids.size(); i++) {
          CachedMemberProfileCard cached = values.get(i);
          if (cached != null) {
            result.put(ids.get(i), toDomain(cached));
          }
        }
      }
      return result;
    } catch (Exception exception) {
      log.warn("멤버 프로필 카드 캐시 조회 실패", exception);
      return Map.of();
    }
  }

  @Override
  public void put(User user) {
    try {
      memberProfileCardRedisTemplate.opsForValue().set(KEY_PREFIX + user.id(), toCache(user), TTL);
    } catch (Exception exception) {
      log.warn("멤버 프로필 카드 캐시 저장 실패. userId: {}", user.id(), exception);
    }
  }

  @Override
  public void evict(Long userId) {
    try {
      memberProfileCardRedisTemplate.delete(KEY_PREFIX + userId);
    } catch (Exception exception) {
      log.warn("멤버 프로필 카드 캐시 삭제 실패. userId: {}", userId, exception);
    }
  }

  private CachedMemberProfileCard toCache(User user) {
    Profile profile = user.profile();
    UserFavor favor = profile.userFavor();

    List<CachedUserActivity> activities =
        user.activities().activities().stream()
            .map(
                a ->
                    new CachedUserActivity(
                        a.id(),
                        a.generation(),
                        a.part() != null ? a.part().name() : null,
                        a.team() != null ? a.team().name() : null,
                        a.role().name(),
                        a.isSopt()))
            .toList();

    List<CachedMemberLink> links =
        profile.links().stream().map(l -> new CachedMemberLink(l.id(), l.title(), l.url())).toList();

    List<CachedMemberCareer> careers =
        profile.careers().stream()
            .map(
                c ->
                    new CachedMemberCareer(
                        c.id(), c.companyName(), c.title(), c.startDate(), c.endDate(), c.isCurrent()))
            .toList();

    return new CachedMemberProfileCard(
        user.id(),
        profile.name(),
        profile.profileImage(),
        profile.birthday(),
        profile.phone(),
        profile.email(),
        profile.address(),
        profile.university(),
        profile.major(),
        profile.introduction(),
        profile.mbti(),
        profile.mbtiDescription(),
        profile.sojuCapacity(),
        profile.interest(),
        profile.idealType(),
        profile.selfIntroduction(),
        profile.skill(),
        profile.allowOfficial(),
        profile.isPhoneBlind(),
        favor != null ? favor.isPourSauceLover() : null,
        favor != null ? favor.isHardPeachLover() : null,
        favor != null ? favor.isMintChocoLover() : null,
        favor != null ? favor.isRedBeanFishBreadLover() : null,
        favor != null ? favor.isSojuLover() : null,
        favor != null ? favor.isRiceTteokLover() : null,
        activities,
        links,
        careers);
  }

  private User toDomain(CachedMemberProfileCard cached) {
    Profile profile =
        new Profile(
            cached.name(),
            cached.email(),
            cached.phone(),
            cached.birthday(),
            cached.profileImage(),
            cached.address(),
            cached.university(),
            cached.major(),
            cached.introduction(),
            cached.mbti(),
            cached.mbtiDescription(),
            cached.sojuCapacity(),
            cached.interest(),
            UserFavor.of(
                cached.isPourSauceLover(),
                cached.isHardPeachLover(),
                cached.isMintChocoLover(),
                cached.isRedBeanFishBreadLover(),
                cached.isSojuLover(),
                cached.isRiceTteokLover()),
            cached.idealType(),
            cached.selfIntroduction(),
            cached.skill(),
            cached.allowOfficial(),
            cached.isPhoneBlind(),
            null,
            cached.links().stream().map(l -> UserLink.of(l.id(), cached.userId(), l.title(), l.url())).toList(),
            cached.careers().stream()
                .map(
                    c ->
                        UserCareer.of(
                            c.id(), cached.userId(), c.companyName(), c.title(), c.startDate(), c.endDate(),
                            c.isCurrent()))
                .toList());

    List<Activity> activities =
        cached.activities().stream()
            .map(
                a ->
                    Activity.of(
                        a.activityId(),
                        a.generation(),
                        a.team() != null ? Team.valueOf(a.team()) : null,
                        a.part() != null ? Part.valueOf(a.part()) : null,
                        Role.valueOf(a.role()),
                        a.isSopt(),
                        null))
            .toList();

    return User.createUser(cached.userId(), null, profile, ActivityList.of(activities), false);
  }
}
