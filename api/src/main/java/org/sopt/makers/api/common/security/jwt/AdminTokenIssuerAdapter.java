package org.sopt.makers.api.common.security.jwt;

import static org.sopt.makers.api.common.security.exception.TokenFailureCode.INVALID_SUBJECT;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.security.authentication.CustomAuthentication;
import org.sopt.makers.api.common.security.exception.TokenException;
import org.sopt.makers.domain.admin.auth.port.AdminTokenIssuerPort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminTokenIssuerAdapter implements AdminTokenIssuerPort {

  private static final String ADMIN_AUTHORITY = "ADMIN";

  private final JwtAccessTokenService jwtAccessTokenService;
  private final JwtRefreshTokenService jwtRefreshTokenService;

  @Override
  public AdminTokenPair issue(Long adminId) {
    CustomAuthentication auth =
        new CustomAuthentication(
            String.valueOf(adminId), null, List.of(new SimpleGrantedAuthority(ADMIN_AUTHORITY)));
    String accessToken = jwtAccessTokenService.generateJwt(auth);
    String refreshToken = jwtRefreshTokenService.generateJwt(auth.getPrincipal());
    return new AdminTokenPair(accessToken, refreshToken);
  }

  @Override
  public AdminTokenPair refresh(String expiredAccessToken, String refreshToken) {
    JwtRefreshToken parsedRefreshToken = jwtRefreshTokenService.parse(refreshToken);
    CustomAuthentication auth = jwtAccessTokenService.parseLenient(expiredAccessToken);
    if (!auth.getPrincipal().equals(parsedRefreshToken.subject())) {
      throw new TokenException(INVALID_SUBJECT);
    }
    String newAccessToken = jwtAccessTokenService.generateJwt(auth);
    String newRefreshToken = jwtRefreshTokenService.generateJwt(auth.getPrincipal());
    return new AdminTokenPair(newAccessToken, newRefreshToken);
  }
}
