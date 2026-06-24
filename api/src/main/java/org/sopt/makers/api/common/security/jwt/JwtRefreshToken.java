package org.sopt.makers.api.common.security.jwt;

import static org.sopt.makers.api.common.security.exception.TokenFailureCode.INVALID_SUBJECT;

import io.jsonwebtoken.Claims;
import org.sopt.makers.api.common.security.exception.TokenException;

public record JwtRefreshToken(Claims claims) {

  public static final String CLAIM_TOKEN_TYPE = "tokenType";

  public static JwtRefreshToken of(final Claims claims) {
    return new JwtRefreshToken(claims);
  }

  public String subject() {
    String subject = claims.getSubject();
    boolean isInvalidSubject = subject == null || subject.isBlank();

    if (isInvalidSubject) {
      throw new TokenException(INVALID_SUBJECT);
    }

    return subject;
  }

  public String tokenType() {
    return claims.get(CLAIM_TOKEN_TYPE, String.class);
  }
}
