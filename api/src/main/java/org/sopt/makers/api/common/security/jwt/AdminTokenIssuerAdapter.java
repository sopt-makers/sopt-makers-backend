package org.sopt.makers.api.common.security.jwt;

import static org.sopt.makers.api.common.security.exception.TokenFailureCode.INVALID_REFRESH_TOKEN;
import static org.sopt.makers.api.common.security.exception.TokenFailureCode.INVALID_SUBJECT;
import static org.sopt.makers.api.common.security.exception.TokenFailureCode.INVALID_TOKEN_TYPE;

import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.security.authentication.CustomAuthentication;
import org.sopt.makers.api.common.security.exception.TokenException;
import org.sopt.makers.domain.admin.auth.port.AdminRefreshTokenRepositoryPort;
import org.sopt.makers.domain.admin.auth.port.AdminTokenIssuerPort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminTokenIssuerAdapter implements AdminTokenIssuerPort {

  private static final String TOKEN_TYPE = "ADMIN";
  private static final String ADMIN_AUTHORITY = "ADMIN";

  private final JwtAccessTokenService jwtAccessTokenService;
  private final JwtRefreshTokenService jwtRefreshTokenService;
  private final AdminRefreshTokenRepositoryPort adminRefreshTokenRepositoryPort;

  @Override
  public AdminTokenPair issue(Long adminId) {
    CustomAuthentication auth =
        new CustomAuthentication(
            String.valueOf(adminId), null, List.of(new SimpleGrantedAuthority(ADMIN_AUTHORITY)));
    String accessToken = jwtAccessTokenService.generateJwt(auth);
    String refreshToken = jwtRefreshTokenService.generateJwt(auth.getPrincipal(), TOKEN_TYPE);
    Duration ttl = Duration.ofSeconds(jwtRefreshTokenService.refreshTokenExpirationSeconds());
    adminRefreshTokenRepositoryPort.save(adminId, refreshToken, ttl);
    return new AdminTokenPair(accessToken, refreshToken);
  }

  @Override
  public AdminTokenPair refresh(String expiredAccessToken, String refreshToken) {
    JwtRefreshToken parsedRefreshToken = jwtRefreshTokenService.parse(refreshToken);

    if (!TOKEN_TYPE.equals(parsedRefreshToken.tokenType())) {
      throw new TokenException(INVALID_TOKEN_TYPE);
    }

    CustomAuthentication auth = jwtAccessTokenService.parseLenient(expiredAccessToken);
    if (!auth.getPrincipal().equals(parsedRefreshToken.subject())) {
      throw new TokenException(INVALID_SUBJECT);
    }

    Long adminId = Long.parseLong(auth.getPrincipal());
    if (!adminRefreshTokenRepositoryPort.exists(adminId, refreshToken)) {
      throw new TokenException(INVALID_REFRESH_TOKEN);
    }

    adminRefreshTokenRepositoryPort.delete(adminId, refreshToken);

    String newAccessToken = jwtAccessTokenService.generateJwt(auth);
    String newRefreshToken = jwtRefreshTokenService.generateJwt(auth.getPrincipal(), TOKEN_TYPE);
    Duration ttl = Duration.ofSeconds(jwtRefreshTokenService.refreshTokenExpirationSeconds());
    adminRefreshTokenRepositoryPort.save(adminId, newRefreshToken, ttl);

    return new AdminTokenPair(newAccessToken, newRefreshToken);
  }
}
