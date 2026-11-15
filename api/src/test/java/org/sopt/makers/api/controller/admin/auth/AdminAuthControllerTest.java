package org.sopt.makers.api.controller.admin.auth;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.servlet.http.Cookie;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.*;
import org.sopt.makers.api.common.config.SecurityProperty;
import org.sopt.makers.api.common.exception.GlobalExceptionHandler;
import org.sopt.makers.api.common.factory.CookieFactory;
import org.sopt.makers.api.common.resolver.CurrentUserIdArgumentResolver;
import org.sopt.makers.api.common.security.authentication.CustomAuthentication;
import org.sopt.makers.domain.admin.auth.*;
import org.sopt.makers.domain.admin.auth.exception.*;
import org.sopt.makers.domain.admin.auth.port.AdminTokenIssuerPort.AdminTokenPair;
import org.sopt.makers.domain.admin.auth.service.AdminAuthService;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AdminAuthControllerTest {
  private final AdminAuthService service = mock(AdminAuthService.class);
  private final SecurityProperty property =
      new SecurityProperty(
          new SecurityProperty.Api(Map.of("test", "key"), List.of("/actuator")),
          new SecurityProperty.Jwt(
              new SecurityProperty.Jwt.Secret(
                  new SecurityProperty.Jwt.Secret.Expiration(3600L, 1209600L),
                  "test-secret",
                  new SecurityProperty.Jwt.Secret.Issuer("test-issuer"))));
  private final MockMvc mvc =
      MockMvcBuilders.standaloneSetup(new AdminAuthController(service, new CookieFactory(property)))
          .setCustomArgumentResolvers(new CurrentUserIdArgumentResolver())
          .setControllerAdvice(new GlobalExceptionHandler())
          .build();

  @AfterEach
  void clearAuthentication() {
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("로그인하면 기존 관리자 정보와 토큰 및 갱신 쿠키를 반환한다")
  void logsIn() throws Exception {
    when(service.login("admin@sopt.org", "password"))
        .thenReturn(
            new AdminAuthService.LoginResult(
                new AdminAccount(1L, "admin@sopt.org", "hash", "관리자", AdminAccountType.SOPT),
                new AdminTokenPair("access", "refresh")));
    mvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"admin@sopt.org\",\"password\":\"password\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.name").value("관리자"))
        .andExpect(jsonPath("$.data.adminStatus").value("SOPT"))
        .andExpect(jsonPath("$.data.accessToken").value("access"))
        .andExpect(jsonPath("$.data.refreshToken").doesNotExist())
        .andExpect(
            header()
                .string(
                    "Set-Cookie",
                    allOf(
                        containsString("refreshToken=refresh"),
                        containsString("Max-Age=1209600"),
                        containsString("HttpOnly"),
                        containsString("Secure"),
                        containsString("SameSite=None"),
                        containsString("Domain=.sopt.org"))));
  }

  @Test
  @DisplayName("Authorization 없이 쿠키만으로 토큰을 갱신한다")
  void refreshesWithCookie() throws Exception {
    when(service.refresh("refresh")).thenReturn(new AdminTokenPair("new-access", "new-refresh"));
    mvc.perform(patch("/api/v1/auth/refresh").cookie(new Cookie("refreshToken", "refresh")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.accessToken").value("new-access"))
        .andExpect(header().string("Set-Cookie", containsString("refreshToken=new-refresh")));
    verify(service).refresh("refresh");
  }

  @Test
  @DisplayName("인증된 관리자의 기존 비밀번호와 새 비밀번호를 전달한다")
  void changesPassword() throws Exception {
    SecurityContextHolder.getContext()
        .setAuthentication(new CustomAuthentication("1", "", List.of()));
    mvc.perform(
            patch("/api/v1/auth/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"oldPassword\":\"old\",\"newPassword\":\"new\"}"))
        .andExpect(status().isOk());
    verify(service).changePassword(1L, "old", "new");
  }

  @Test
  @DisplayName("비밀번호가 잘못되면 기존 401 응답을 유지한다")
  void rejectsWrongPassword() throws Exception {
    when(service.login("admin@sopt.org", "wrong"))
        .thenThrow(new AdminAuthException(AdminAuthFailure.INVALID_PASSWORD));
    mvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"admin@sopt.org\",\"password\":\"wrong\"}"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("빈 로그인 정보는 400을 반환한다")
  void rejectsBlankLogin() throws Exception {
    mvc.perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"\",\"password\":\"\"}"))
        .andExpect(status().isBadRequest());
    verifyNoInteractions(service);
  }
}
