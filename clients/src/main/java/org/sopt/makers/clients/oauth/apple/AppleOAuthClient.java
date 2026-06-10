package org.sopt.makers.clients.oauth.apple;

import static org.sopt.makers.clients.oauth.OAuthConstant.APPLE_PUBLIC_KEY_SET_URL;

import com.nimbusds.jose.jwk.JWKSet;
import org.sopt.makers.clients.oauth.JwkSetCache;
import org.springframework.stereotype.Component;

@Component
public class AppleOAuthClient {

  private final JwkSetCache jwkSetCache = new JwkSetCache(APPLE_PUBLIC_KEY_SET_URL);

  public JWKSet getPublicKeySet() {
    return jwkSetCache.get();
  }

  public JWKSet refreshPublicKeySet() {
    return jwkSetCache.refresh();
  }
}
