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
import lombok.RequiredArgsConstructor;
import org.sopt.makers.clients.config.OAuthProperty;
import org.sopt.makers.domain.auth.exception.AuthException;
import org.sopt.makers.domain.auth.exception.AuthFailure;
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
      throw new AuthException(AuthFailure.INVALID_ID_TOKEN);
    }
  }

  private JWK findMatchJWK(SignedJWT jwt) {
    JWKSet jwkSet = googleOAuthClient.getPublicKeySet();
    String kid = jwt.getHeader().getKeyID();
    return jwkSet.getKeys().stream()
        .filter(jwk -> jwk.getKeyID().equals(kid))
        .findFirst()
        .orElseThrow(() -> new AuthException(AuthFailure.NOT_FOUND_AVAILABLE_PUBLIC_KEY_SET));
  }

  private void verifyGoogleIdToken(SignedJWT jwt, JWK jwk) throws ParseException {
    try {
      JWTClaimsSet claims = jwt.getJWTClaimsSet();
      JWSVerifier verifier = new RSASSAVerifier(jwk.toRSAKey());
      boolean isValidSignature = jwt.verify(verifier);
      boolean isCorrectIssuer = GOOGLE_ISSUER.equals(claims.getIssuer());
      boolean isCorrectAudience =
          claims.getAudience().contains(oAuthProperty.google().client().id());
      boolean isNotExpired = claims.getExpirationTime().after(Date.from(Instant.now()));

      if (!(isValidSignature && isCorrectIssuer && isCorrectAudience && isNotExpired)) {
        throw new AuthException(AuthFailure.INVALID_ID_TOKEN);
      }
    } catch (JOSEException e) {
      throw new AuthException(AuthFailure.INVALID_ID_TOKEN);
    }
  }
}
