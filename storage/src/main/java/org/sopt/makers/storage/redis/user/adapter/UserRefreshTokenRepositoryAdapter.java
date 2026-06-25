package org.sopt.makers.storage.redis.user.adapter;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.auth.port.UserRefreshTokenRepositoryPort;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRefreshTokenRepositoryAdapter implements UserRefreshTokenRepositoryPort {

  private static final String KEY_PREFIX = "user:refresh:";

  private final StringRedisTemplate stringRedisTemplate;

  @Override
  public void save(Long userId, String refreshToken, Duration ttl) {
    stringRedisTemplate.opsForValue().set(key(userId, refreshToken), refreshToken, ttl);
  }

  @Override
  public boolean exists(Long userId, String refreshToken) {
    return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key(userId, refreshToken)));
  }

  @Override
  public void delete(Long userId, String refreshToken) {
    stringRedisTemplate.delete(key(userId, refreshToken));
  }

  @Override
  public void deleteAll(Long userId) {
    ScanOptions scanOptions =
        ScanOptions.scanOptions().match(KEY_PREFIX + userId + ":*").count(1000).build();

    try (Cursor<String> keys = stringRedisTemplate.scan(scanOptions)) {
      while (keys.hasNext()) {
        stringRedisTemplate.delete(keys.next());
      }
    }
  }

  private String key(Long userId, String refreshToken) {
    return KEY_PREFIX + userId + ":" + refreshToken;
  }
}
