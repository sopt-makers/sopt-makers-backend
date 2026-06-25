package org.sopt.makers.api.common.security;

import java.util.List;

public final class SecurityConstant {

  private SecurityConstant() {}

  public static final String ROLES = "roles";
  public static final String TOKEN_HEADER = "Bearer ";
  public static final String API_KEY_HEADER = "X-Api-Key";
  public static final String SERVICE_NAME_HEADER = "X-Service-Name";
  public static final String ROLE_PREFIX = "ROLE_";
  public static final String INTERNAL_SERVICE = "INTERNAL_SERVICE";
  public static final String ADMIN = "ADMIN";
  public static final String PATTERN_ALL = "/**";

  public static final List<String> JWT_WHITELIST =
      List.of(
          "/api/v1/auth",
          "/api/v1/social/accounts",
          "/api/v1/admin/auth/login",
          "/api/v1/admin/auth/refresh",
          "/error",
          "/swagger-ui.html",
          "/swagger-ui",
          "/v3/api-docs");
}
