package org.sopt.makers.clients.oauth.google;

import static org.sopt.makers.clients.oauth.OAuthConstant.GOOGLE_ISSUER;

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
public class GoogleOAuthService {

  private final OAuthProperty oAuthProperty;
  private final GoogleOAuthClient googleOAuthClient;

  public String getIdentifierByToken(String token) {
    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      JWK targetJwk = findMatchJWK(signedJWT);
      verifyGoogleIdToken(signedJWT, targetJwk);
      return signedJWT.getJWTClaimsSet().getSubject();
    } catch (ParseException e) {
      throw new OAuthException(OAuthFailure.INVALID_ID_TOKEN);
    }
  }

  private JWK findMatchJWK(SignedJWT jwt) {
    String kid = jwt.getHeader().getKeyID();
    if (kid == null) {
      throw new OAuthException(OAuthFailure.INVALID_ID_TOKEN);
    }

    JWKSet jwkSet = googleOAuthClient.getPublicKeySet();
    return findMatchJWK(jwkSet, kid)
        .orElseGet(
            () ->
                findMatchJWK(googleOAuthClient.refreshPublicKeySet(), kid)
                    .orElseThrow(() -> new OAuthException(OAuthFailure.INVALID_ID_TOKEN)));
  }

  private Optional<JWK> findMatchJWK(JWKSet jwkSet, String kid) {
    return jwkSet.getKeys().stream().filter(jwk -> Objects.equals(jwk.getKeyID(), kid)).findFirst();
  }

  private void verifyGoogleIdToken(SignedJWT jwt, JWK jwk) throws ParseException {
    try {
      JWTClaimsSet claims = jwt.getJWTClaimsSet();
      JWSVerifier verifier = new RSASSAVerifier(jwk.toRSAKey());
      boolean isValidSignature = jwt.verify(verifier);
      boolean isCorrectIssuer = GOOGLE_ISSUER.equals(claims.getIssuer());
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
    return audiences != null && audiences.contains(oAuthProperty.google().client().id());
  }

  private boolean verifyExpirationTime(Date expirationTime) {
    return expirationTime != null && expirationTime.after(Date.from(Instant.now()));
  }
}
