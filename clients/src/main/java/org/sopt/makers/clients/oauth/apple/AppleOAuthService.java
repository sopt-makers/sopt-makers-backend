package org.sopt.makers.clients.oauth.apple;

import static org.sopt.makers.clients.oauth.OAuthConstant.APPLE_ISSUER;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.clients.config.OAuthProperty;
import org.sopt.makers.clients.oauth.exception.OAuthException;
import org.sopt.makers.clients.oauth.exception.OAuthFailure;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppleOAuthService {

  private final OAuthProperty oAuthProperty;
  private final AppleOAuthClient appleOAuthClient;

  public String getIdentifierByToken(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      JWK targetJwk = findMatchJWK(signedJWT);
      verifyAppleIdToken(signedJWT, targetJwk);
      return signedJWT.getJWTClaimsSet().getSubject();
    } catch (ParseException e) {
      throw new OAuthException(OAuthFailure.INVALID_ID_TOKEN);
    }
  }

  private JWK findMatchJWK(SignedJWT jwt) {
    String kid = jwt.getHeader().getKeyID();
    String alg =
        jwt.getHeader().getAlgorithm() == null ? null : jwt.getHeader().getAlgorithm().getName();
    if (kid == null || alg == null) {
      throw new OAuthException(OAuthFailure.INVALID_ID_TOKEN);
    }

    JWKSet jwkSet = appleOAuthClient.getPublicKeySet();
    return findMatchJWK(jwkSet, kid, alg)
        .orElseGet(
            () ->
                findMatchJWK(appleOAuthClient.refreshPublicKeySet(), kid, alg)
                    .orElseThrow(() -> new OAuthException(OAuthFailure.INVALID_ID_TOKEN)));
  }

  private Optional<JWK> findMatchJWK(JWKSet jwkSet, String kid, String alg) {
    return jwkSet.getKeys().stream()
        .filter(
            jwk ->
                Objects.equals(jwk.getKeyID(), kid) && Objects.equals(getAlgorithmName(jwk), alg))
        .findFirst();
  }

  private void verifyAppleIdToken(SignedJWT jwt, JWK jwk) throws ParseException {
    try {
      JWTClaimsSet claims = jwt.getJWTClaimsSet();
      JWSVerifier verifier = new RSASSAVerifier(jwk.toRSAKey());
      boolean isValidSignature = jwt.verify(verifier);
      boolean isCorrectIssuer = APPLE_ISSUER.equals(claims.getIssuer());
      boolean isCorrectAudience = verifyAudience(claims.getAudience());
      boolean isNotExpired = verifyExpirationTime(claims.getExpirationTime());
      boolean hasSubject = claims.getSubject() != null;

      if (!(isValidSignature
          && isCorrectIssuer
          && isCorrectAudience
          && isNotExpired
          && hasSubject)) {
        throw new OAuthException(OAuthFailure.INVALID_ID_TOKEN);
      }
    } catch (JOSEException e) {
      throw new OAuthException(OAuthFailure.INVALID_ID_TOKEN);
    }
  }

  private boolean verifyAudience(List<String> audiences) {
    return audiences != null
        && (audiences.contains(oAuthProperty.apple().webAud())
            || audiences.contains(oAuthProperty.apple().appAud()));
  }

  private boolean verifyExpirationTime(Date expirationTime) {
    return expirationTime != null && expirationTime.after(Date.from(Instant.now()));
  }

  private String getAlgorithmName(JWK jwk) {
    return jwk.getAlgorithm() == null ? null : jwk.getAlgorithm().getName();
  }
}
