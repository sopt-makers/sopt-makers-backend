package org.sopt.makers.storage.redis.user.adapter;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.auth.port.UserRefreshTokenRepositoryPort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRefreshTokenRepositoryAdapter implements UserRefreshTokenRepositoryPort {

  private static final String KEY_PREFIX = "user:refresh:";

  private final StringRedisTemplate stringRedisTemplate;

  @Override
  public void save(Long userId, String refreshToken, Duration ttl) {
    String key = KEY_PREFIX + userId;
    stringRedisTemplate.opsForSet().add(key, refreshToken);
    stringRedisTemplate.expire(key, ttl);
  }

  @Override
  public boolean exists(Long userId, String refreshToken) {
    return Boolean.TRUE.equals(
        stringRedisTemplate.opsForSet().isMember(KEY_PREFIX + userId, refreshToken));
  }

  @Override
  public void delete(Long userId, String refreshToken) {
    stringRedisTemplate.opsForSet().remove(KEY_PREFIX + userId, refreshToken);
  }

  @Override
  public void deleteAll(Long userId) {
    stringRedisTemplate.delete(KEY_PREFIX + userId);
  }
}
