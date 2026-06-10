package org.sopt.makers.api.common.security.jwt;

import io.jsonwebtoken.Claims;
import java.util.List;
import org.sopt.makers.api.common.security.authentication.CustomAuthentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public record JwtAccessToken(Claims claims) {

  private static final String ROLES = "roles";

  public static JwtAccessToken of(final Claims claims) {
    return new JwtAccessToken(claims);
  }

  public CustomAuthentication parse() {
    List<?> rawRoles = claims.get(ROLES, List.class);
    List<SimpleGrantedAuthority> authorities =
        rawRoles.stream().map(role -> new SimpleGrantedAuthority((String) role)).toList();
    return new CustomAuthentication(claims.getSubject(), null, authorities);
  }
}
