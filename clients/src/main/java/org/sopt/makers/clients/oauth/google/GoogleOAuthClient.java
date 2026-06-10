package org.sopt.makers.clients.oauth.google;

import static org.sopt.makers.clients.oauth.OAuthConstant.GOOGLE_PUBLIC_KEY_SET_URL;

import com.nimbusds.jose.jwk.JWKSet;
import org.sopt.makers.clients.oauth.JwkSetCache;
import org.springframework.stereotype.Component;

@Component
public class GoogleOAuthClient {

  private final JwkSetCache jwkSetCache = new JwkSetCache(GOOGLE_PUBLIC_KEY_SET_URL);

  public JWKSet getPublicKeySet() {
    return jwkSetCache.get();
  }

  public JWKSet refreshPublicKeySet() {
    return jwkSetCache.refresh();
  }
}
