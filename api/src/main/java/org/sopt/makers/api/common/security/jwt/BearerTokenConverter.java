package org.sopt.makers.api.common.security.jwt;

import static org.sopt.makers.api.common.security.SecurityConstant.TOKEN_HEADER;

public final class BearerTokenConverter {

  private BearerTokenConverter() {}

  public static String extract(final String token) {
    return token.substring(TOKEN_HEADER.length());
  }

  public static String addPrefix(final String token) {
    return TOKEN_HEADER + token;
  }
}
