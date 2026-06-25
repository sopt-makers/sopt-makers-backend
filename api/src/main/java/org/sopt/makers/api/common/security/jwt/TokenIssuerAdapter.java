package org.sopt.makers.api.common.security.jwt;

import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.security.jwt.JwtTokenRotator.JwtTokenPair;
import org.sopt.makers.domain.auth.port.TokenIssuerPort;
import org.sopt.makers.domain.auth.port.UserRefreshTokenRepositoryPort;
import org.sopt.makers.domain.user.Role;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenIssuerAdapter implements TokenIssuerPort {

  private static final String TOKEN_TYPE = "USER";

  private final JwtTokenRotator jwtTokenRotator;
  private final UserRefreshTokenRepositoryPort userRefreshTokenRepositoryPort;

  @Override
  public TokenPair issue(Long userId, Role role) {
    JwtTokenPair tokenPair =
        jwtTokenRotator.issue(
            userId, role.name(), TOKEN_TYPE, userRefreshTokenRepositoryPort::save);
    return new TokenPair(tokenPair.accessToken(), tokenPair.refreshToken());
  }

  @Override
  public TokenPair refresh(String expiredAccessToken, String refreshToken) {
    JwtTokenPair tokenPair =
        jwtTokenRotator.refresh(
            expiredAccessToken,
            refreshToken,
            TOKEN_TYPE,
            userRefreshTokenRepositoryPort::delete,
            userRefreshTokenRepositoryPort::save);
    return new TokenPair(tokenPair.accessToken(), tokenPair.refreshToken());
  }
}
