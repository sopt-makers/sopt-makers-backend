package org.sopt.makers.clients.oauth.google;

import static org.sopt.makers.clients.oauth.OAuthConstant.GOOGLE_PUBLIC_KEY_SET_URL;

import com.nimbusds.jose.jwk.JWK;
import java.util.Optional;
import java.util.function.Predicate;
import org.sopt.makers.clients.oauth.JwkSetCache;
import org.springframework.stereotype.Component;

@Component
public class GoogleOAuthClient {

  private final JwkSetCache jwkSetCache = new JwkSetCache(GOOGLE_PUBLIC_KEY_SET_URL);

  public Optional<JWK> findPublicKey(Predicate<JWK> matcher) {
    return jwkSetCache.find(matcher);
  }
}
