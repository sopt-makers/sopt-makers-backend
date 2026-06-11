package org.sopt.makers.api.common.security.jwt;

import static org.sopt.makers.api.common.security.exception.TokenFailureCode.INVALID_SUBJECT;

import io.jsonwebtoken.Claims;
import org.sopt.makers.api.common.security.exception.TokenException;

public record JwtRefreshToken(Claims claims) {

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
}
