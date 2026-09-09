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

  public static final String[] CORS_ALLOWED_ORIGINS = {
    "http://localhost:3000",
    "http://localhost:5173",
    "https://sopt-internal-dev.sopt.org",
    "https://playground.sopt.org",
    "https://sopt-auth-frontend-test.pages.dev",
    "https://sopt-org-frontend-git-test-daeuns-projects-025386f4.vercel.app",
    "https://sopt-operation-frontend-3yc.pages.dev"
  };

  /**
   * 레거시 InternalOpenApiController 호환 경로. 레거시에서도 Spring Security 계층에서는 인증을 요구하지
   * 않았고(anyRequest().permitAll()), 쓰기 성격의 2개 엔드포인트만 자체적으로 apiKey 헤더를 검증했다
   * (InternalOpenApiController 참고).
   */
  public static final String LEGACY_INTERNAL_OPEN_API_PATH = "/internal/api/v1";

  public static final List<String> JWT_WHITELIST =
      List.of(
          "/api/v1/auth",
          "/api/v1/official",
          "/api/v1/social/accounts",
          "/api/v1/admin/auth/login",
          "/api/v1/admin/auth/refresh",
          "/api/v1/admin/banners/images",
          "/slack/emoji",
          "/api/v2/admin/soptamp",
          "/api/v2/admin/notification",
          "/api/v2/config",
          "/api/v2/firebase",
          "/error",
          "/swagger-ui.html",
          "/swagger-ui",
          "/v3/api-docs",
          LEGACY_INTERNAL_OPEN_API_PATH);

  public static final List<String> JWT_OPTIONAL_PATHS =
      List.of(
          "/api/v2/user/main",
          "/api/v2/home/app-service",
          "/api/v2/home/tab-app-service",
          "/api/v2/home/floating-button",
          "/api/v2/home/review-form");

  public static final String INTERNAL_API_PATH = "/api/v1/internal";
  public static final List<String> API_KEY_SECURED_PATHS = List.of(INTERNAL_API_PATH);
}
