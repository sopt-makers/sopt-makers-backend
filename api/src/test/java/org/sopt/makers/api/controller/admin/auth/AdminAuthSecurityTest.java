package org.sopt.makers.api.controller.admin.auth;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.servlet.http.Cookie;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sopt.makers.api.common.config.SecurityConfig;
import org.sopt.makers.api.common.config.SecurityProperty;
import org.sopt.makers.api.common.exception.GlobalExceptionHandler;
import org.sopt.makers.api.common.factory.CookieFactory;
import org.sopt.makers.api.common.resolver.CurrentUserIdArgumentResolver;
import org.sopt.makers.api.common.security.authentication.CustomAuthentication;
import org.sopt.makers.api.common.security.filter.ApiKeyAuthenticationFilter;
import org.sopt.makers.api.common.security.filter.AuthenticationExceptionFilter;
import org.sopt.makers.api.common.security.filter.JwtAuthenticationFilter;
import org.sopt.makers.api.common.security.jwt.JwtAccessTokenService;
import org.sopt.makers.domain.admin.auth.*;
import org.sopt.makers.domain.admin.auth.port.AdminTokenIssuerPort.AdminTokenPair;
import org.sopt.makers.domain.admin.auth.service.AdminAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

@SpringJUnitWebConfig(AdminAuthSecurityTest.Config.class)
@ActiveProfiles("prod")
class AdminAuthSecurityTest {
  private static final String BASE = "/api/v1/auth";

  @Autowired private WebApplicationContext context;
  @Autowired private AdminAuthService service;
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
  @DisplayName("로그인은 JWT 없이 호출할 수 있다")
  void allowsLogin() throws Exception {
    when(service.login("admin@sopt.org", "password"))
        .thenReturn(
            new AdminAuthService.LoginResult(
                new AdminAccount(1L, "admin@sopt.org", "hash", "관리자", AdminAccountType.SOPT),
                new AdminTokenPair("access", "refresh")));
    mvc.perform(
            post(BASE + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"admin@sopt.org\",\"password\":\"password\"}"))
        .andExpect(status().isOk());
    verifyNoInteractions(tokenService);
  }

  @Test
  @DisplayName("토큰 갱신은 Authorization 없이 쿠키로 인증한다")
  void allowsRefresh() throws Exception {
    when(service.refresh("refresh")).thenReturn(new AdminTokenPair("new-access", "new-refresh"));
    mvc.perform(patch(BASE + "/refresh").cookie(new Cookie("refreshToken", "refresh")))
        .andExpect(status().isOk());
    verify(service).refresh("refresh");
    verifyNoInteractions(tokenService);
  }

  @Test
  @DisplayName("관리자 토큰으로 비밀번호를 변경할 수 있다")
  void allowsPasswordChange() throws Exception {
    mvc.perform(
            patch(BASE + "/password")
                .header("Authorization", "Bearer admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"oldPassword\":\"old\",\"newPassword\":\"new\"}"))
        .andExpect(status().isOk());
    verify(service).changePassword(1L, "old", "new");
  }

  @Test
  @DisplayName("일반 사용자 토큰으로 관리자 비밀번호를 변경할 수 없다")
  void rejectsUser() throws Exception {
    mvc.perform(
            patch(BASE + "/password")
                .header("Authorization", "Bearer user")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"oldPassword\":\"old\",\"newPassword\":\"new\"}"))
        .andExpect(status().isForbidden());
    verifyNoInteractions(service);
  }

  @Test
  @DisplayName("비밀번호 변경은 JWT 없이 호출할 수 없다")
  void requiresJwt() throws Exception {
    mvc.perform(patch(BASE + "/password")).andExpect(status().isUnauthorized());
    verifyNoInteractions(service);
  }

  @Configuration
  @EnableWebMvc
  @EnableSpringDataWebSupport
  @Import({
    SecurityConfig.class,
    AdminAuthController.class,
    CookieFactory.class,
    GlobalExceptionHandler.class,
    JwtAuthenticationFilter.class,
    ApiKeyAuthenticationFilter.class,
    AuthenticationExceptionFilter.class
  })
  static class Config implements WebMvcConfigurer {
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
      resolvers.add(new CurrentUserIdArgumentResolver());
    }

    @Bean
    AdminAuthService adminAuthService() {
      return mock(AdminAuthService.class);
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
          new SecurityProperty.Api(Map.of("monitoring", "test-key"), List.of("/actuator")),
          new SecurityProperty.Jwt(
              new SecurityProperty.Jwt.Secret(
                  new SecurityProperty.Jwt.Secret.Expiration(3600L, 1209600L),
                  "test-secret",
                  new SecurityProperty.Jwt.Secret.Issuer("test-issuer"))));
    }
  }
}
