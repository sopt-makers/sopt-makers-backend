package org.sopt.makers.domain.auth.port;

import org.sopt.makers.core.type.OAuthPlatform;

public interface OAuthAuthenticatorPort {

  /** OAuth ID 토큰을 검증하고 플랫폼 식별자(sub)를 반환한다. */
  String getIdentifier(String idToken, OAuthPlatform platform);
}
