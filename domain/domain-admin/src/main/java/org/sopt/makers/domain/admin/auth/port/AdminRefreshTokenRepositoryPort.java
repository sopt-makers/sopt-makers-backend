package org.sopt.makers.domain.admin.auth.port;

import java.time.Duration;

public interface AdminRefreshTokenRepositoryPort {

  void save(Long adminId, String refreshToken, Duration ttl);

  boolean delete(Long adminId, String refreshToken);

  void deleteAll(Long adminId);
}
