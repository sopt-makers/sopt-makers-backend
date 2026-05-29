package org.sopt.makers.api.common.util;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.sopt.makers.api.common.config.SecurityProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieUtil {

  private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
  private static final String COOKIE_DOMAIN = ".sopt.org";
  private static final String SAME_SITE_NONE = "None";
  private static final String ROOT_PATH = "/";

  private final SecurityProperty securityProperty;

  public HttpHeaders setRefreshToken(final String refreshToken) {
    long durationSeconds = securityProperty.jwt().secret().expiration().refreshTokenExpiration();
    ResponseCookie cookie =
        ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
            .httpOnly(true)
            .secure(true)
            .sameSite(SAME_SITE_NONE)
            .domain(COOKIE_DOMAIN)
            .path(ROOT_PATH)
            .maxAge(Duration.ofSeconds(durationSeconds))
            .build();

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
    return headers;
  }
}
