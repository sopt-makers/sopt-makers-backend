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
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.clients.config.OAuthProperty;
import org.sopt.makers.domain.auth.exception.AuthException;
import org.sopt.makers.domain.auth.exception.AuthFailure;
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
      throw new AuthException(AuthFailure.INVALID_ID_TOKEN);
    }
  }

  private JWK findMatchJWK(SignedJWT jwt) {
    String kid = jwt.getHeader().getKeyID();
    String alg = jwt.getHeader().getAlgorithm().getName();
    JWKSet jwkSet = appleOAuthClient.getPublicKeySet();
    return findMatchJWK(jwkSet, kid, alg)
        .orElseGet(
            () ->
                findMatchJWK(appleOAuthClient.refreshPublicKeySet(), kid, alg)
                    .orElseThrow(
                        () -> new AuthException(AuthFailure.NOT_FOUND_AVAILABLE_PUBLIC_KEY_SET)));
  }

  private Optional<JWK> findMatchJWK(JWKSet jwkSet, String kid, String alg) {
    return jwkSet.getKeys().stream()
        .filter(jwk -> jwk.getKeyID().equals(kid) && jwk.getAlgorithm().getName().equals(alg))
        .findFirst();
  }

  private void verifyAppleIdToken(SignedJWT jwt, JWK jwk) throws ParseException {
    try {
      JWTClaimsSet claims = jwt.getJWTClaimsSet();
      JWSVerifier verifier = new RSASSAVerifier(jwk.toRSAKey());
      boolean isValidSignature = jwt.verify(verifier);
      boolean isCorrectIssuer = APPLE_ISSUER.equals(claims.getIssuer());
      boolean isCorrectAudience = verifyAudience(claims.getAudience());
      boolean isNotExpired = claims.getExpirationTime().after(Date.from(Instant.now()));

      if (!(isValidSignature && isCorrectIssuer && isCorrectAudience && isNotExpired)) {
        throw new AuthException(AuthFailure.INVALID_ID_TOKEN);
      }
    } catch (JOSEException e) {
      throw new AuthException(AuthFailure.INVALID_ID_TOKEN);
    }
  }

  private boolean verifyAudience(List<String> audiences) {
    return audiences.contains(oAuthProperty.apple().webAud())
        || audiences.contains(oAuthProperty.apple().appAud());
  }
}
