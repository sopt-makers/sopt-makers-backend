package org.sopt.makers.api.common.security.jwt;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.security.jwt.JwtTokenRotator.JwtTokenPair;
import org.sopt.makers.domain.admin.auth.port.AdminRefreshTokenRepositoryPort;
import org.sopt.makers.domain.admin.auth.port.AdminTokenIssuerPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminTokenIssuerAdapter implements AdminTokenIssuerPort {

  private static final String TOKEN_TYPE = "ADMIN";
  private static final String ADMIN_AUTHORITY = "ADMIN";

  private final JwtTokenRotator jwtTokenRotator;
  private final AdminRefreshTokenRepositoryPort adminRefreshTokenRepositoryPort;

  @Override
  public AdminTokenPair issue(Long adminId) {
    JwtTokenPair tokenPair =
        jwtTokenRotator.issue(
            adminId, ADMIN_AUTHORITY, TOKEN_TYPE, adminRefreshTokenRepositoryPort::save);
    return new AdminTokenPair(tokenPair.accessToken(), tokenPair.refreshToken());
  }

  @Override
  public AdminTokenPair refresh(String expiredAccessToken, String refreshToken) {
    JwtTokenPair tokenPair =
        jwtTokenRotator.refresh(
            expiredAccessToken,
            refreshToken,
            TOKEN_TYPE,
            adminRefreshTokenRepositoryPort::delete,
            adminRefreshTokenRepositoryPort::save);
    return new AdminTokenPair(tokenPair.accessToken(), tokenPair.refreshToken());
  }
}
