package org.sopt.makers.api.common.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.cors.DefaultCorsProcessor;

class CorsConfigTest {
  @ParameterizedTest
  @ValueSource(
      strings = {
        "https://sopt.org", "https://www.dev.sopt.org",
        "https://admins.sopt.org", "https://dev.admins.sopt.org"
      })
  @DisplayName("공식 홈페이지와 관리자 페이지의 API 키 사전 요청을 허용한다")
  void allowsPageOrigins(String origin) throws Exception {
    MockHttpServletRequest request = preflight(origin);
    MockHttpServletResponse response = new MockHttpServletResponse();

    boolean allowed =
        new DefaultCorsProcessor()
            .processRequest(
                new CorsConfig().corsConfigurationSource().getCorsConfiguration(request),
                request,
                response);

    assertThat(allowed).isTrue();
    assertThat(response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN)).isEqualTo(origin);
    assertThat(response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS))
        .containsIgnoringCase("api-key")
        .containsIgnoringCase("X-Api-Key")
        .containsIgnoringCase("X-Service-Name")
        .containsIgnoringCase("Authorization");
    assertThat(response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS)).isEqualTo("true");
  }

  @Test
  @DisplayName("등록되지 않은 도메인의 사전 요청을 차단한다")
  void rejectsUnknownOrigin() throws Exception {
    MockHttpServletRequest request = preflight("https://unregistered.example.com");
    MockHttpServletResponse response = new MockHttpServletResponse();

    boolean allowed =
        new DefaultCorsProcessor()
            .processRequest(
                new CorsConfig().corsConfigurationSource().getCorsConfiguration(request),
                request,
                response);

    assertThat(allowed).isFalse();
    assertThat(response.getStatus()).isEqualTo(403);
    assertThat(response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN)).isNull();
  }

  private MockHttpServletRequest preflight(String origin) {
    MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/v2/admin");
    request.addHeader(HttpHeaders.ORIGIN, origin);
    request.addHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST");
    request.addHeader(
        HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS,
        "Content-Type, Authorization, api-key, X-Api-Key, X-Service-Name");
    return request;
  }
}
