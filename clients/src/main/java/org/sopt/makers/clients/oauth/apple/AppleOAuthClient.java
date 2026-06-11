package org.sopt.makers.clients.oauth.apple;

import static org.sopt.makers.clients.oauth.OAuthConstant.APPLE_PUBLIC_KEY_SET_URL;

import com.nimbusds.jose.jwk.JWK;
import java.util.Optional;
import java.util.function.Predicate;
import org.sopt.makers.clients.oauth.JwkSetCache;
import org.springframework.stereotype.Component;

@Component
public class AppleOAuthClient {

  private final JwkSetCache jwkSetCache = new JwkSetCache(APPLE_PUBLIC_KEY_SET_URL);

  public Optional<JWK> findPublicKey(Predicate<JWK> matcher) {
    return jwkSetCache.find(matcher);
  }
}
