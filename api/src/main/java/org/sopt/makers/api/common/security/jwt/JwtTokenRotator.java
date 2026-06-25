package org.sopt.makers.api.common.security.jwt;

import static org.sopt.makers.api.common.security.exception.TokenFailureCode.INVALID_REFRESH_TOKEN;
import static org.sopt.makers.api.common.security.exception.TokenFailureCode.INVALID_SUBJECT;
import static org.sopt.makers.api.common.security.exception.TokenFailureCode.INVALID_TOKEN_TYPE;

import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.security.authentication.CustomAuthentication;
import org.sopt.makers.api.common.security.exception.TokenException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenRotator {

  private final JwtAccessTokenService jwtAccessTokenService;
  private final JwtRefreshTokenService jwtRefreshTokenService;

  public JwtTokenPair issue(
      Long subjectId, String authority, String tokenType, RefreshTokenSaver refreshTokenSaver) {
    CustomAuthentication auth =
        new CustomAuthentication(
            String.valueOf(subjectId), null, List.of(new SimpleGrantedAuthority(authority)));
    String accessToken = jwtAccessTokenService.generateJwt(auth);
    String refreshToken = jwtRefreshTokenService.generateJwt(auth.getPrincipal(), tokenType);
    Duration ttl = Duration.ofSeconds(jwtRefreshTokenService.refreshTokenExpirationSeconds());
    refreshTokenSaver.save(subjectId, refreshToken, ttl);
    return new JwtTokenPair(accessToken, refreshToken);
  }

  public JwtTokenPair refresh(
      String expiredAccessToken,
      String refreshToken,
      String tokenType,
      RefreshTokenDeleter refreshTokenDeleter,
      RefreshTokenSaver refreshTokenSaver) {
    JwtRefreshToken parsedRefreshToken = jwtRefreshTokenService.parse(refreshToken);

    if (!tokenType.equals(parsedRefreshToken.tokenType())) {
      throw new TokenException(INVALID_TOKEN_TYPE);
    }

    CustomAuthentication auth = jwtAccessTokenService.parseLenient(expiredAccessToken);
    if (!auth.getPrincipal().equals(parsedRefreshToken.subject())) {
      throw new TokenException(INVALID_SUBJECT);
    }

    Long subjectId = Long.parseLong(auth.getPrincipal());
    boolean isDeleted = refreshTokenDeleter.delete(subjectId, refreshToken);
    if (!isDeleted) {
      throw new TokenException(INVALID_REFRESH_TOKEN);
    }

    String newAccessToken = jwtAccessTokenService.generateJwt(auth);
    String newRefreshToken = jwtRefreshTokenService.generateJwt(auth.getPrincipal(), tokenType);
    Duration ttl = Duration.ofSeconds(jwtRefreshTokenService.refreshTokenExpirationSeconds());
    refreshTokenSaver.save(subjectId, newRefreshToken, ttl);

    return new JwtTokenPair(newAccessToken, newRefreshToken);
  }

  public record JwtTokenPair(String accessToken, String refreshToken) {}

  @FunctionalInterface
  public interface RefreshTokenSaver {

    void save(Long subjectId, String refreshToken, Duration ttl);
  }

  @FunctionalInterface
  public interface RefreshTokenDeleter {

    boolean delete(Long subjectId, String refreshToken);
  }
}
