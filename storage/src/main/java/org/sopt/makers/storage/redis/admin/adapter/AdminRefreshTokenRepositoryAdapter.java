package org.sopt.makers.storage.redis.admin.adapter;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.admin.auth.port.AdminRefreshTokenRepositoryPort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminRefreshTokenRepositoryAdapter implements AdminRefreshTokenRepositoryPort {

  private static final String KEY_PREFIX = "admin:refresh:";

  private final StringRedisTemplate stringRedisTemplate;

  @Override
  public void save(Long adminId, String refreshToken, Duration ttl) {
    String key = KEY_PREFIX + adminId;
    stringRedisTemplate.opsForSet().add(key, refreshToken);
    stringRedisTemplate.expire(key, ttl);
  }

  @Override
  public boolean exists(Long adminId, String refreshToken) {
    return Boolean.TRUE.equals(
        stringRedisTemplate.opsForSet().isMember(KEY_PREFIX + adminId, refreshToken));
  }

  @Override
  public void delete(Long adminId, String refreshToken) {
    stringRedisTemplate.opsForSet().remove(KEY_PREFIX + adminId, refreshToken);
  }

  @Override
  public void deleteAll(Long adminId) {
    stringRedisTemplate.delete(KEY_PREFIX + adminId);
  }
}
