package org.sopt.makers.storage.redis.user.adapter;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.core.type.Part;
import org.sopt.makers.domain.user.Activity;
import org.sopt.makers.domain.user.ActivityList;
import org.sopt.makers.domain.user.Profile;
import org.sopt.makers.domain.user.Role;
import org.sopt.makers.domain.user.Team;
import org.sopt.makers.domain.user.User;
import org.sopt.makers.domain.user.port.UserCacheRepositoryPort;
import org.sopt.makers.storage.redis.user.cache.CachedUserActivity;
import org.sopt.makers.storage.redis.user.cache.CachedUserProfile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserCacheRepositoryAdapter implements UserCacheRepositoryPort {

  private static final String KEY_PREFIX = "user:profile:";
  private static final Duration TTL = Duration.ofDays(1);

  private final RedisTemplate<String, CachedUserProfile> userProfileRedisTemplate;

  @Override
  public Map<Long, User> getAllPresent(List<Long> userIds) {
    if (userIds.isEmpty()) {
      return Map.of();
    }

    List<String> keys = userIds.stream().map(id -> KEY_PREFIX + id).toList();
    List<CachedUserProfile> values = userProfileRedisTemplate.opsForValue().multiGet(keys);

    Map<Long, User> result = new HashMap<>();
    if (values != null) {
      for (int i = 0; i < userIds.size(); i++) {
        CachedUserProfile cached = values.get(i);
        if (cached != null) {
          result.put(userIds.get(i), toDomain(cached));
        }
      }
    }
    return result;
  }

  @Override
  public void put(User user) {
    userProfileRedisTemplate.opsForValue().set(KEY_PREFIX + user.getId(), toCache(user), TTL);
  }

  @Override
  public void evict(Long userId) {
    userProfileRedisTemplate.delete(KEY_PREFIX + userId);
  }

  private CachedUserProfile toCache(User user) {
    Profile profile = user.getProfile();
    ActivityList activityList = user.getActivities();
    return new CachedUserProfile(
        user.getId(),
        profile.name(),
        profile.profileImage().orElse(null),
        profile.birthday(),
        profile.phone(),
        profile.email().orElse(null),
        activityList.getLastActivity().getGeneration(),
        activityList.getActivities().stream()
            .map(
                a ->
                    new CachedUserActivity(
                        a.getId(),
                        a.getGeneration(),
                        a.getPart().name(),
                        a.optionalTeam().map(Team::name).orElse(null),
                        a.getRole().name(),
                        a.isSopt()))
            .toList());
  }

  private User toDomain(CachedUserProfile cached) {
    Profile profile =
        Profile.of(
            cached.name(),
            cached.email(),
            cached.phone(),
            cached.birthday(),
            cached.profileImage());
    List<Activity> activities =
        cached.activities().stream()
            .map(
                a ->
                    Activity.of(
                        a.activityId(),
                        a.generation(),
                        a.team() != null ? Team.valueOf(a.team()) : null,
                        Part.valueOf(a.part()),
                        Role.valueOf(a.role()),
                        a.isSopt(),
                        null))
            .toList();
    // 캐시에서 복원된 User는 SocialAccount 없이 조회 전용으로만 사용된다
    return User.createUser(cached.userId(), null, profile, ActivityList.of(activities), false);
  }
}
