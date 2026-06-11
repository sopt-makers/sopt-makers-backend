package org.sopt.makers.api.common.security.authentication;

import java.util.Collection;
import java.util.Objects;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

@NullMarked
public class CustomAuthentication extends UsernamePasswordAuthenticationToken {

  private final String principal;

  public CustomAuthentication(final String principal, final Object credentials) {
    super(Objects.requireNonNull(principal), credentials);
    this.principal = principal;
  }

  public CustomAuthentication(
      final String principal,
      final Object credentials,
      final Collection<? extends GrantedAuthority> authorities) {
    super(Objects.requireNonNull(principal), credentials, authorities);
    this.principal = principal;
  }

  @Override
  public String getPrincipal() {
    return principal;
  }
}
