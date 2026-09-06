package org.sopt.makers.api.common.security.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.api.common.security.jwt.JwtAccessTokenService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;

@DisplayName("JwtAuthenticationFilter 테스트")
class JwtAuthenticationFilterTest {

  private final JwtAuthenticationFilter filter =
      new JwtAuthenticationFilter(mock(JwtAccessTokenService.class));

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("선택 인증 경로에 헤더가 없으면 필터를 건너뛴다")
  void skipsOptionalPathWithoutHeader() {
    assertThat(filter.shouldNotFilter(request("/api/v2/user/main", null))).isTrue();
  }

  @Test
  @DisplayName("선택 인증 경로에 Bearer 가 아닌 헤더가 오면 토큰 없음으로 보고 건너뛴다")
  void skipsOptionalPathWithNonBearerHeader() {
    assertThat(filter.shouldNotFilter(request("/api/v2/home/app-service", ""))).isTrue();
    assertThat(filter.shouldNotFilter(request("/api/v2/home/app-service", "Basic abc"))).isTrue();
  }

  @Test
  @DisplayName("선택 인증 경로에 Bearer 토큰이 오면 필터를 탄다")
  void filtersOptionalPathWithBearerToken() {
    assertThat(filter.shouldNotFilter(request("/api/v2/home/floating-button", "Bearer token")))
        .isFalse();
  }

  @Test
  @DisplayName("선택 인증 경로가 아니면 헤더가 없어도 필터를 탄다")
  void filtersRequiredPathWithoutHeader() {
    assertThat(filter.shouldNotFilter(request("/api/v2/home/description", null))).isFalse();
  }

  private static MockHttpServletRequest request(String uri, String authorization) {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", uri);
    request.setRequestURI(uri);
    if (authorization != null) {
      request.addHeader("Authorization", authorization);
    }
    return request;
  }
}
