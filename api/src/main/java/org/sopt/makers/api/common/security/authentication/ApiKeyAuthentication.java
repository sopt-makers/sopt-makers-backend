package org.sopt.makers.api.common.security.authentication;

import static org.sopt.makers.api.common.security.SecurityConstant.INTERNAL_SERVICE;
import static org.sopt.makers.api.common.security.SecurityConstant.ROLE_PREFIX;

import java.util.List;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class ApiKeyAuthentication extends AbstractAuthenticationToken {

  private final String apiKey;
  private final String serviceName;

  public ApiKeyAuthentication(final String apiKey, final String serviceName) {
    super(List.of(new SimpleGrantedAuthority(ROLE_PREFIX + INTERNAL_SERVICE)));
    this.apiKey = apiKey;
    this.serviceName = serviceName;
    super.setAuthenticated(true);
  }

  @Override
  public Object getCredentials() {
    return apiKey;
  }

  @Override
  public Object getPrincipal() {
    return serviceName;
  }
}
