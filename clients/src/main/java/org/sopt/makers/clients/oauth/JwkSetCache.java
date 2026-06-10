package org.sopt.makers.clients.oauth;

import com.nimbusds.jose.jwk.JWKSet;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import org.sopt.makers.domain.auth.exception.AuthException;
import org.sopt.makers.domain.auth.exception.AuthFailure;

public class JwkSetCache {

  private static final Duration CACHE_TTL = Duration.ofHours(24);

  private final String jwkSetUrl;

  private volatile JWKSet cachedJwkSet;
  private volatile Instant cachedAt = Instant.EPOCH;

  public JwkSetCache(String jwkSetUrl) {
    this.jwkSetUrl = jwkSetUrl;
  }

  public JWKSet get() {
    if (isCacheValid()) {
      return cachedJwkSet;
    }

    synchronized (this) {
      if (isCacheValid()) {
        return cachedJwkSet;
      }

      try {
        return loadAndCache();
      } catch (AuthException e) {
        if (cachedJwkSet != null) {
          return cachedJwkSet;
        }
        throw e;
      }
    }
  }

  public synchronized JWKSet refresh() {
    return loadAndCache();
  }

  private boolean isCacheValid() {
    return cachedJwkSet != null && cachedAt.plus(CACHE_TTL).isAfter(Instant.now());
  }

  private JWKSet loadAndCache() {
    try {
      JWKSet jwkSet = JWKSet.load(URI.create(jwkSetUrl).toURL());
      cachedJwkSet = jwkSet;
      cachedAt = Instant.now();
      return jwkSet;
    } catch (Exception e) {
      throw new AuthException(AuthFailure.NOT_FOUND_AVAILABLE_PUBLIC_KEY_SET);
    }
  }
}
