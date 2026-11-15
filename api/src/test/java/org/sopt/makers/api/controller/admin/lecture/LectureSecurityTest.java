package org.sopt.makers.api.controller.admin.lecture;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.sopt.makers.api.common.config.SecurityConfig;
import org.sopt.makers.api.common.config.SecurityProperty;
import org.sopt.makers.api.common.exception.GlobalExceptionHandler;
import org.sopt.makers.api.common.security.authentication.CustomAuthentication;
import org.sopt.makers.api.common.security.filter.ApiKeyAuthenticationFilter;
import org.sopt.makers.api.common.security.filter.AuthenticationExceptionFilter;
import org.sopt.makers.api.common.security.filter.JwtAuthenticationFilter;
import org.sopt.makers.api.common.security.jwt.JwtAccessTokenService;
import org.sopt.makers.domain.admin.lecture.service.LectureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@SpringJUnitWebConfig(LectureSecurityTest.Config.class)
@ActiveProfiles("prod")
class LectureSecurityTest {
  private static final String BASE = "/api/v1/lectures";

  @Autowired private WebApplicationContext context;
  @Autowired private LectureService service;
  @Autowired private JwtAccessTokenService tokenService;
  private MockMvc mvc;

  @BeforeEach
  void setUp() {
    reset(service, tokenService);
    when(tokenService.parse("Bearer admin"))
        .thenAnswer(
            invocation ->
                new CustomAuthentication("1", "", List.of(new SimpleGrantedAuthority("ADMIN"))));
    when(tokenService.parse("Bearer user"))
        .thenAnswer(
            invocation ->
                new CustomAuthentication("2", "", List.of(new SimpleGrantedAuthority("USER"))));
    mvc =
        MockMvcBuilders.webAppContextSetup(context)
            .addFilters(context.getBean(FilterChainProxy.class))
            .build();
  }

  @Test
  @DisplayName("ADMIN 권한이 있는 사용자는 세션 관리 API에 접근할 수 있다")
  void allowsAdmin() throws Exception {
    when(service.getLectures(39, null)).thenReturn(List.of());
    when(service.getAttendanceSummaries(List.of())).thenReturn(Map.of());

    mvc.perform(get(BASE).param("generation", "39").header("Authorization", "Bearer admin"))
        .andExpect(status().isOk());
    verify(service).getLectures(39, null);
  }

  @ParameterizedTest
  @MethodSource("adminRoutes")
  @DisplayName("일반 사용자 토큰으로는 세션 관리 API에 접근할 수 없다")
  void rejectsNonAdmin(HttpMethod method, String path) throws Exception {
    mvc.perform(request(method, path).header("Authorization", "Bearer user"))
        .andExpect(status().isForbidden());
    verifyNoInteractions(service);
  }

  @ParameterizedTest
  @MethodSource("adminRoutes")
  @DisplayName("세션 관리 API는 JWT 없이 접근할 수 없다")
  void requiresJwt(HttpMethod method, String path) throws Exception {
    mvc.perform(request(method, path)).andExpect(status().isUnauthorized());
    verifyNoInteractions(service);
  }

  private static Stream<Arguments> adminRoutes() {
    return Stream.of(
        Arguments.of(HttpMethod.GET, BASE),
        Arguments.of(HttpMethod.POST, BASE),
        Arguments.of(HttpMethod.GET, BASE + "/1"),
        Arguments.of(HttpMethod.PATCH, BASE + "/attendance"),
        Arguments.of(HttpMethod.PATCH, BASE + "/1"),
        Arguments.of(HttpMethod.DELETE, BASE + "/1"),
        Arguments.of(HttpMethod.GET, BASE + "/detail/1"));
  }

  @Configuration
  @EnableWebMvc
  @Import({
    SecurityConfig.class,
    LectureController.class,
    GlobalExceptionHandler.class,
    JwtAuthenticationFilter.class,
    ApiKeyAuthenticationFilter.class,
    AuthenticationExceptionFilter.class
  })
  static class Config {
    @Bean
    LectureService lectureService() {
      return mock(LectureService.class);
    }

    @Bean
    JwtAccessTokenService tokenService() {
      return mock(JwtAccessTokenService.class);
    }

    @Bean
    ObjectMapper objectMapper() {
      return JsonMapper.builder().build();
    }

    @Bean
    SecurityProperty securityProperty() {
      return new SecurityProperty(
          new SecurityProperty.Api(Map.of("monitoring", "test-key"), List.of("/actuator")), null);
    }
  }
}
