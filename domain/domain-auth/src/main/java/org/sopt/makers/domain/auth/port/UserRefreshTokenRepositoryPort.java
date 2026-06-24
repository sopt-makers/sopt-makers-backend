package org.sopt.makers.domain.auth.port;

import java.time.Duration;

public interface UserRefreshTokenRepositoryPort {

  void save(Long userId, String refreshToken, Duration ttl);

  boolean exists(Long userId, String refreshToken);

  void delete(Long userId, String refreshToken);

  void deleteAll(Long userId);
}
