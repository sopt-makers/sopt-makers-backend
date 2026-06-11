package org.sopt.makers.api.common.security.jwt;

import static org.sopt.makers.api.common.security.exception.TokenFailureCode.TOKEN_PARSE_FAILED;

import io.jsonwebtoken.Claims;
import java.util.List;
import org.sopt.makers.api.common.security.authentication.CustomAuthentication;
import org.sopt.makers.api.common.security.exception.TokenException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public record JwtAccessToken(Claims claims) {

  private static final String ROLES = "roles";

  public static JwtAccessToken of(final Claims claims) {
    return new JwtAccessToken(claims);
  }

  public CustomAuthentication parse() {
    List<?> rawRoles = claims.get(ROLES, List.class);
    List<SimpleGrantedAuthority> authorities = parseAuthorities(rawRoles);
    return new CustomAuthentication(claims.getSubject(), null, authorities);
  }

  private List<SimpleGrantedAuthority> parseAuthorities(final List<?> rawRoles) {
    if (rawRoles == null) {
      throw new TokenException(TOKEN_PARSE_FAILED);
    }

    return rawRoles.stream().map(this::parseAuthority).toList();
  }

  private SimpleGrantedAuthority parseAuthority(final Object role) {
    if (!(role instanceof String roleName)) {
      throw new TokenException(TOKEN_PARSE_FAILED);
    }

    return new SimpleGrantedAuthority(roleName);
  }
}
