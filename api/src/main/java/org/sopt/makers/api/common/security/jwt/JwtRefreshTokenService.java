package org.sopt.makers.api.common.security.jwt;

import static org.sopt.makers.api.common.security.exception.TokenFailureCode.TOKEN_EXPIRED;
import static org.sopt.makers.api.common.security.exception.TokenFailureCode.TOKEN_PARSE_FAILED;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.config.SecurityProperty;
import org.sopt.makers.api.common.security.exception.TokenException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtRefreshTokenService {

  private final SecretKey jwtSecretKey;
  private final SecurityProperty securityProperty;

  public String generateJwt(final String subject) {
    Date now = new Date();
    Date expiration =
        new Date(
            now.getTime()
                + securityProperty.jwt().secret().expiration().refreshTokenExpiration() * 1000L);

    return Jwts.builder()
        .id(UUID.randomUUID().toString())
        .subject(subject)
        .issuer(securityProperty.jwt().secret().issuer().issuerName())
        .issuedAt(now)
        .expiration(expiration)
        .signWith(jwtSecretKey)
        .compact();
  }

  public JwtRefreshToken parse(final String requestToken) {
    try {
      Claims claims =
          Jwts.parser()
              .verifyWith(jwtSecretKey)
              .requireIssuer(securityProperty.jwt().secret().issuer().issuerName())
              .build()
              .parseSignedClaims(requestToken)
              .getPayload();
      JwtRefreshToken refreshToken = JwtRefreshToken.of(claims);
      refreshToken.subject();
      return refreshToken;
    } catch (ExpiredJwtException e) {
      throw new TokenException(TOKEN_EXPIRED);
    } catch (JwtException e) {
      throw new TokenException(TOKEN_PARSE_FAILED);
    }
  }
}
