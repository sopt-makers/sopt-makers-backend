package org.sopt.makers.api.common.security.authentication;

import java.util.Collection;
import java.util.Objects;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

@NullMarked
public class CustomAuthentication extends UsernamePasswordAuthenticationToken {

  public CustomAuthentication(final Object principal, final Object credentials) {
    super(principal, credentials);
  }

  public CustomAuthentication(
      final Object principal,
      final Object credentials,
      final Collection<? extends GrantedAuthority> authorities) {
    super(principal, credentials, authorities);
  }

  @Override
  public String getPrincipal() {
    return Objects.requireNonNull((String) super.getPrincipal());
  }
}
