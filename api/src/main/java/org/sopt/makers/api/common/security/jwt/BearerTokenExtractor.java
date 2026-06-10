package org.sopt.makers.api.common.security.jwt;

import static org.sopt.makers.api.common.security.SecurityConstant.TOKEN_HEADER;
import static org.sopt.makers.api.common.security.exception.TokenFailureCode.INVALID_PREFIX;

import org.sopt.makers.api.common.security.exception.TokenException;

public final class BearerTokenExtractor {

  private BearerTokenExtractor() {}

  public static String extract(final String token) {
    validatePrefix(token);
    return token.substring(TOKEN_HEADER.length());
  }

  public static String addPrefix(final String token) {
    return TOKEN_HEADER + token;
  }

  public static void validatePrefix(final String token) {
    if (token == null || !token.startsWith(TOKEN_HEADER)) {
      throw new TokenException(INVALID_PREFIX);
    }
  }
}
