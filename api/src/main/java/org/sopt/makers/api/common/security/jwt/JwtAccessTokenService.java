package org.sopt.makers.api.common.security.jwt;

import static org.sopt.makers.api.common.security.SecurityConstant.ROLES;
import static org.sopt.makers.api.common.security.exception.TokenFailureCode.TOKEN_EXPIRED;
import static org.sopt.makers.api.common.security.exception.TokenFailureCode.TOKEN_PARSE_FAILED;
import static org.sopt.makers.api.common.security.jwt.BearerTokenConverter.extract;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.config.SecurityProperty;
import org.sopt.makers.api.common.security.authentication.CustomAuthentication;
import org.sopt.makers.api.common.security.exception.TokenException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAccessTokenService {
  private final SecretKey jwtSecretKey;
  private final SecurityProperty securityProperty;

  public String generateJwt(final CustomAuthentication authentication) {
    Date now = new Date();
    Date expiration =
        new Date(
            now.getTime()
                + securityProperty.jwt().secret().expiration().accessTokenExpiration() * 1000L);
    List<String> roles =
        authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

    return Jwts.builder()
        .subject(authentication.getPrincipal())
        .issuer(securityProperty.jwt().secret().issuer().issuerName())
        .issuedAt(now)
        .expiration(expiration)
        .claim(ROLES, roles)
        .signWith(jwtSecretKey)
        .compact();
  }

  public CustomAuthentication parse(final String requestToken) {
    try {
      String token = extract(requestToken);
      Claims claims =
          Jwts.parser()
              .verifyWith(jwtSecretKey)
              .requireIssuer(securityProperty.jwt().secret().issuer().issuerName())
              .build()
              .parseSignedClaims(token)
              .getPayload();
      return JwtAccessToken.of(claims).parse();
    } catch (ExpiredJwtException e) {
      throw new TokenException(TOKEN_EXPIRED);
    } catch (JwtException e) {
      throw new TokenException(TOKEN_PARSE_FAILED);
    }
  }

  public CustomAuthentication parseLenient(final String requestToken) {
    try {
      String token = extract(requestToken);
      Claims claims =
          Jwts.parser().verifyWith(jwtSecretKey).build().parseSignedClaims(token).getPayload();
      return JwtAccessToken.of(claims).parse();
    } catch (ExpiredJwtException e) {
      return JwtAccessToken.of(e.getClaims()).parse();
    } catch (JwtException e) {
      throw new TokenException(TOKEN_PARSE_FAILED);
    }
  }
}
