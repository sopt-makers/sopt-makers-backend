package org.sopt.makers.clients.oauth;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import java.net.URI;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.sopt.makers.clients.oauth.exception.OAuthException;
import org.sopt.makers.clients.oauth.exception.OAuthFailure;

public class JwkSetCache {

  private static final Duration CACHE_TTL = Duration.ofHours(24);
  private static final Duration STALE_CACHE_GRACE_PERIOD = Duration.ofMinutes(5);
  private static final Duration KID_MISS_REFRESH_BACKOFF = Duration.ofMinutes(5);

  private final Supplier<JWKSet> jwkSetLoader;
  private final Clock clock;

  private volatile JWKSet cachedJwkSet;
  private volatile Instant cachedAt = Instant.EPOCH;
  private volatile Instant lastKidMissRefreshAt = Instant.EPOCH;

  public JwkSetCache(String jwkSetUrl) {
    this(() -> loadFromUrl(jwkSetUrl), Clock.systemUTC());
  }

  JwkSetCache(Supplier<JWKSet> jwkSetLoader, Clock clock) {
    this.jwkSetLoader = jwkSetLoader;
    this.clock = clock;
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
      } catch (OAuthException e) {
        if (isCacheWithinGracePeriod()) {
          return cachedJwkSet;
        }
        throw e;
      }
    }
  }

  public Optional<JWK> find(Predicate<JWK> matcher) {
    JWKSet jwkSet = get();
    Optional<JWK> cachedJwk = find(jwkSet, matcher);
    if (cachedJwk.isPresent()) {
      return cachedJwk;
    }

    return find(refreshAfterMissIfAllowed(matcher), matcher);
  }

  private JWKSet refreshAfterMissIfAllowed(Predicate<JWK> matcher) {
    synchronized (this) {
      if (find(cachedJwkSet, matcher).isPresent()) {
        return cachedJwkSet;
      }

      Instant now = Instant.now(clock);
      if (lastKidMissRefreshAt.plus(KID_MISS_REFRESH_BACKOFF).isAfter(now)) {
        return cachedJwkSet;
      }

      lastKidMissRefreshAt = now;
      try {
        return loadAndCache();
      } catch (OAuthException e) {
        if (isCacheWithinGracePeriod()) {
          return cachedJwkSet;
        }
        throw e;
      }
    }
  }

  private Optional<JWK> find(JWKSet jwkSet, Predicate<JWK> matcher) {
    return jwkSet.getKeys().stream().filter(matcher).findFirst();
  }

  private boolean isCacheValid() {
    return cachedJwkSet != null && cachedAt.plus(CACHE_TTL).isAfter(Instant.now(clock));
  }

  private boolean isCacheWithinGracePeriod() {
    return cachedJwkSet != null
        && cachedAt.plus(CACHE_TTL).plus(STALE_CACHE_GRACE_PERIOD).isAfter(Instant.now(clock));
  }

  private JWKSet loadAndCache() {
    try {
      JWKSet jwkSet = jwkSetLoader.get();
      cachedJwkSet = jwkSet;
      cachedAt = Instant.now(clock);
      return jwkSet;
    } catch (Exception e) {
      throw new OAuthException(OAuthFailure.PUBLIC_KEY_SET_FETCH_FAILED);
    }
  }

  private static JWKSet loadFromUrl(String jwkSetUrl) {
    try {
      return JWKSet.load(URI.create(jwkSetUrl).toURL());
    } catch (Exception e) {
      throw new OAuthException(OAuthFailure.PUBLIC_KEY_SET_FETCH_FAILED);
    }
  }
}
