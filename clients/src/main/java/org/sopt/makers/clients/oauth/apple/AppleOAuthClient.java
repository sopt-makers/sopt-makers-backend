package org.sopt.makers.clients.oauth.apple;

import static org.sopt.makers.clients.oauth.OAuthConstant.APPLE_PUBLIC_KEY_SET_URL;

import com.nimbusds.jose.jwk.JWKSet;
import org.sopt.makers.domain.auth.exception.AuthException;
import org.sopt.makers.domain.auth.exception.AuthFailure;
import org.springframework.stereotype.Component;

@Component
public class AppleOAuthClient {

  public JWKSet getPublicKeySet() {
    try {
      return JWKSet.load(new java.net.URI(APPLE_PUBLIC_KEY_SET_URL).toURL());
    } catch (Exception e) {
      throw new AuthException(AuthFailure.NOT_FOUND_AVAILABLE_PUBLIC_KEY_SET);
    }
  }
}
