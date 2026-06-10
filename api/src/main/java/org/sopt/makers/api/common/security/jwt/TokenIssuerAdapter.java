package org.sopt.makers.api.common.security.jwt;

import static org.sopt.makers.api.common.security.exception.TokenFailureCode.INVALID_SUBJECT;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.security.authentication.CustomAuthentication;
import org.sopt.makers.api.common.security.exception.TokenException;
import org.sopt.makers.domain.auth.port.TokenIssuerPort;
import org.sopt.makers.domain.user.Role;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenIssuerAdapter implements TokenIssuerPort {

  private final JwtAccessTokenService jwtAccessTokenService;
  private final JwtRefreshTokenService jwtRefreshTokenService;

  @Override
  public TokenPair issue(Long userId, Role role) {
    CustomAuthentication auth =
        new CustomAuthentication(
            String.valueOf(userId), null, List.of(new SimpleGrantedAuthority(role.name())));
    String accessToken = jwtAccessTokenService.generateJwt(auth);
    String refreshToken = jwtRefreshTokenService.generateJwt(auth.getPrincipal());
    return new TokenPair(accessToken, refreshToken);
  }

  @Override
  public TokenPair refresh(String expiredAccessToken, String refreshToken) {
    String refreshTokenSubject = jwtRefreshTokenService.parse(refreshToken);
    CustomAuthentication auth = jwtAccessTokenService.parseLenient(expiredAccessToken);
    boolean isInvalidSubject = !auth.getPrincipal().equals(refreshTokenSubject);

    if (isInvalidSubject) {
      throw new TokenException(INVALID_SUBJECT);
    }

    String newAccessToken = jwtAccessTokenService.generateJwt(auth);
    String newRefreshToken = jwtRefreshTokenService.generateJwt(auth.getPrincipal());
    return new TokenPair(newAccessToken, newRefreshToken);
  }
}
