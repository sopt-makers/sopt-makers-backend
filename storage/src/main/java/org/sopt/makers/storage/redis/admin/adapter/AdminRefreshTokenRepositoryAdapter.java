package org.sopt.makers.storage.redis.admin.adapter;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.domain.admin.auth.port.AdminRefreshTokenRepositoryPort;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminRefreshTokenRepositoryAdapter implements AdminRefreshTokenRepositoryPort {

  private static final String KEY_PREFIX = "admin:refresh:";

  private final StringRedisTemplate stringRedisTemplate;

  @Override
  public void save(Long adminId, String refreshToken, Duration ttl) {
    stringRedisTemplate.opsForValue().set(key(adminId, refreshToken), refreshToken, ttl);
  }

  @Override
  public boolean exists(Long adminId, String refreshToken) {
    return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key(adminId, refreshToken)));
  }

  @Override
  public void delete(Long adminId, String refreshToken) {
    stringRedisTemplate.delete(key(adminId, refreshToken));
  }

  @Override
  public void deleteAll(Long adminId) {
    ScanOptions scanOptions =
        ScanOptions.scanOptions().match(KEY_PREFIX + adminId + ":*").count(1000).build();

    try (Cursor<String> keys = stringRedisTemplate.scan(scanOptions)) {
      while (keys.hasNext()) {
        stringRedisTemplate.delete(keys.next());
      }
    }
  }

  private String key(Long adminId, String refreshToken) {
    return KEY_PREFIX + adminId + ":" + refreshToken;
  }
}
