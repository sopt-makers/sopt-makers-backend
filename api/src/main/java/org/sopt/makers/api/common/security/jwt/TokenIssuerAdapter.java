package org.sopt.makers.api.common.security.jwt;

import static org.sopt.makers.api.common.security.exception.TokenFailureCode.INVALID_REFRESH_TOKEN;
import static org.sopt.makers.api.common.security.exception.TokenFailureCode.INVALID_SUBJECT;
import static org.sopt.makers.api.common.security.exception.TokenFailureCode.INVALID_TOKEN_TYPE;

import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.security.authentication.CustomAuthentication;
import org.sopt.makers.api.common.security.exception.TokenException;
import org.sopt.makers.domain.auth.port.TokenIssuerPort;
import org.sopt.makers.domain.auth.port.UserRefreshTokenRepositoryPort;
import org.sopt.makers.domain.user.Role;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenIssuerAdapter implements TokenIssuerPort {

  private static final String TOKEN_TYPE = "USER";

  private final JwtAccessTokenService jwtAccessTokenService;
  private final JwtRefreshTokenService jwtRefreshTokenService;
  private final UserRefreshTokenRepositoryPort userRefreshTokenRepositoryPort;

  @Override
  public TokenPair issue(Long userId, Role role) {
    CustomAuthentication auth =
        new CustomAuthentication(
            String.valueOf(userId), null, List.of(new SimpleGrantedAuthority(role.name())));
    String accessToken = jwtAccessTokenService.generateJwt(auth);
    String refreshToken = jwtRefreshTokenService.generateJwt(auth.getPrincipal(), TOKEN_TYPE);
    Duration ttl = Duration.ofSeconds(jwtRefreshTokenService.refreshTokenExpirationSeconds());
    userRefreshTokenRepositoryPort.save(userId, refreshToken, ttl);
    return new TokenPair(accessToken, refreshToken);
  }

  @Override
  public TokenPair refresh(String expiredAccessToken, String refreshToken) {
    JwtRefreshToken parsedRefreshToken = jwtRefreshTokenService.parse(refreshToken);

    if (!TOKEN_TYPE.equals(parsedRefreshToken.tokenType())) {
      throw new TokenException(INVALID_TOKEN_TYPE);
    }

    CustomAuthentication auth = jwtAccessTokenService.parseLenient(expiredAccessToken);
    if (!auth.getPrincipal().equals(parsedRefreshToken.subject())) {
      throw new TokenException(INVALID_SUBJECT);
    }

    Long userId = Long.parseLong(auth.getPrincipal());
    boolean isDeleted = userRefreshTokenRepositoryPort.delete(userId, refreshToken);
    if (!isDeleted) {
      throw new TokenException(INVALID_REFRESH_TOKEN);
    }

    String newAccessToken = jwtAccessTokenService.generateJwt(auth);
    String newRefreshToken = jwtRefreshTokenService.generateJwt(auth.getPrincipal(), TOKEN_TYPE);
    Duration ttl = Duration.ofSeconds(jwtRefreshTokenService.refreshTokenExpirationSeconds());
    userRefreshTokenRepositoryPort.save(userId, newRefreshToken, ttl);

    return new TokenPair(newAccessToken, newRefreshToken);
  }
}
